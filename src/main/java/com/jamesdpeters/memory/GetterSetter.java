package com.jamesdpeters.memory;

import com.jamesdpeters.Utils;
import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDStatus;
import com.jamesdpeters.gpu.Tiles;

public class GetterSetter {

    interface Getter {
        int get(MemoryBus.Bank bank, int address);
    }

    interface Setter {
        void set(MemoryBus.Bank bank, int address, int value);
    }

    private Getter getter;
    private Setter setter;

    public int get(MemoryBus.Bank bank, int address){
        return getter.get(bank,address);
    }

    public void set(MemoryBus.Bank bank, int address, int value){
        setter.set(bank, address, value);
    }

    public GetterSetter(Getter getter, Setter setter){
        this.getter = getter;
        this.setter = setter;
    }


    public static final GetterSetter VRAM = new GetterSetter(
            MemoryBus.Bank::getDirectByte,
            (bank, address, value) -> {
                bank.setDirectByte(address,value);

//                System.out.println("Value = "+Utils.intToString(value));

                int normalised = address & 0xFFFE;
//                System.out.println("Normalised Adr: "+Utils.intToString(normalised));
//                System.out.println("Normalised+1 Adr: "+Utils.intToString(normalised+1));
//                System.out.println("VRAM: "+bank.toByteString());
                int b1 = bank.getDirectByte(normalised);
                int b2 = bank.getDirectByte(normalised+1);

                int tileIndex = address / 16;
                int rowIndex = (address % 16) / 2;

//                System.out.println();
//                System.out.println("B1: "+Utils.intToBinaryString(b1)+" B2: "+Utils.intToBinaryString(b2));
                for(int pixel=0; pixel<8; pixel++){
//                    int mask = 1 << (7 - pixel);
//                    System.out.println("Mask: "+Utils.intToBinaryString(mask));

                    int lsb = Utils.getBit(b1,7-pixel);
                    int msb = Utils.getBit(b2,7-pixel);
//                    System.out.println("Pos: "+pixel+" lsb: "+Utils.intToBinaryString(lsb)+" msb: "+Utils.intToBinaryString(msb));

                    Tiles.PixelValue pixelValue = Tiles.getPixelValue(lsb,msb);
                    Tiles.setTilePixel(tileIndex,rowIndex,pixel,pixelValue);
                    //System.out.println("Set Tile: "+tileIndex+" row: "+rowIndex+" pixel: "+pixel+" value: "+pixelValue);
                }
            }
    );

    public static final GetterSetter DEFAULT = new GetterSetter(MemoryBus.Bank::getDirectByte, MemoryBus.Bank::setDirectByte);

    public static final GetterSetter IOREGISTER = new GetterSetter(MemoryBus.Bank::getDirectByte, (bank, address, value) -> {

        //Remove Boot Rom when called.
        if(address == 0x50) {
            System.out.println("Boot Rom Enabled: "+(value != 1));
            MemoryBus.isBootRomEnabled = (value != 1);
            System.exit(-1);
        }

        // BGP - BG Palette Data (R/W)
        if(address == 0x47) {
            Tiles.setColorPalette(value);
        }

        //LCDControl (LCD Control Register)
        if(address == 0x40){
            LCDControl.set(value);
        }

        if(address == 0x41){
            LCDStatus.set(value);
        }
//        System.out.println("SETTING IO REGISTER ADDRESS: "+ Utils.intToString(address)+" to value: "+Utils.intToString(value));
        bank.setDirectByte(address,value);
//        System.out.println(bank.toByteString());
    });
}
