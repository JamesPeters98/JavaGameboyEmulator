package com.jamesdpeters.memory;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Joypad;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.registers.IE;
import com.jamesdpeters.cpu.registers.IF;
import com.jamesdpeters.gpu.PixelValue;
import com.jamesdpeters.gpu.Tile;
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

                int normalised = address & 0xFFFE;
                int b1 = bank.getDirectByte(normalised);
                int b2 = bank.getDirectByte(normalised+1);

                int tileIndex = address / 16;
                int rowIndex = (address % 16) / 2;

                for(int pixel=0; pixel<8; pixel++){
                    int lsb = Utils.getBit(b1,7-pixel);
                    int msb = Utils.getBit(b2,7-pixel);
                    PixelValue pixelValue = PixelValue.getPixelValue(lsb,msb);
                    Tiles.setTilePixel(tileIndex,rowIndex,pixel,pixelValue);
                }
                Tile tile = Tiles.getTile(tileIndex);
                GameBoy.instance.backgroundMap.setTile(tile);
            }
    );

    public static final GetterSetter DEFAULT = new GetterSetter(MemoryBus.Bank::getDirectByte, MemoryBus.Bank::setDirectByte);

    public static final GetterSetter IOREGISTER = new GetterSetter(MemoryBus.Bank::getDirectByte, (bank, address, value) -> {

        /* Set Joypad select (Bottom 4 bits are read only) */
        /* Return since Joypad class handles the setting of the register. */
        if(address == 0x00) {
            Joypad.set(value);
            return;
        }

        switch(address){
            /* Remove Boot Rom when called. */
            case 0x50:
                System.out.println("Boot Rom Enabled: "+(value != 1));
                MemoryBus.isBootRomEnabled = (value != 1);
                System.out.println();
                break;

            case 0x46:
                System.out.println("Writing to OAM DMA Transfer Register! "+Utils.intToString(value));
                break;

            /* BGP - BG Palette Data (R/W) */
            case 0x47:
                PixelValue.setBackgroundPalette(value);
                break;

            /* OBP0 - Object Palette 0 Data (R/W) */
            case 0x48:
                PixelValue.setOBJ1Palette(value);
                break;

            /* OBP1 - Object Palette 1 Data (R/W) */
            case 0x49:
                PixelValue.setOBJ2Palette(value);
                break;


            /* LCDControl (LCD Control Register) */
            case 0x40:
                LCDControl.set(value);
                break;

            /* LCDStatus */
            case 0x41:
                LCDStatus.set(value);
                break;

            /* Interrupts Request */
            case 0x0F:
                IF.set(value);
                break;

        }
        bank.setDirectByte(address,value);
    });

    public static final GetterSetter INTERRUPTS = new GetterSetter(MemoryBus.Bank::getDirectByte, (bank, address, value) -> {
       if(address == 0){
           IE.set(value);
       }
       bank.setDirectByte(address,value);
    });

    //Return default value and don't write to memory.
    public static final GetterSetter UNUSEABLE = new GetterSetter((bank,i) -> 0xFF, ((bank, address, value) -> {
        //Do nothing
    }));

    public static final GetterSetter HRAM = new GetterSetter(
            (bank, address) -> {
                int value = bank.getDirectByte(address);
                if(address == 0x05) {
//                    System.out.println("Getting FF85 "+value);
//                    System.out.println("IME: "+GameBoy.getCpu().getRegisters().IME);
//                    System.out.println("IE: "+IE.getINSTANCE());
//                    System.out.println("IF: "+IF.getINSTANCE());
                }

                return value;
                },
            (bank, address, value) -> {
                if(address == 0x05){
//                    System.out.println("Setting FF85: "+value);
//                    System.out.println(GameBoy.getCpu().getRegisters());
//                    Utils.waitForInput();
                }
                bank.setDirectByte(address,value);
            });
}
