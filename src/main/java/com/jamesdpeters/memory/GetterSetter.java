package com.jamesdpeters.memory;

import com.jamesdpeters.Utils;
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

                System.out.println();
                System.out.println("Set Tile: "+tileIndex);
                for(int pixel=0; pixel<8; pixel++){
                    int mask = 1 << (7 - pixel);
                    int lsb = b1 & mask;
                    int msb = b2 & mask;

                    Tiles.PixelValue pixelValue = Tiles.getPixelValue(lsb,msb);
                    Tiles.setTilePixel(tileIndex,rowIndex,pixel,pixelValue);
                }
            }
    );

    public static final GetterSetter DEFAULT = new GetterSetter(MemoryBus.Bank::getDirectByte, MemoryBus.Bank::setDirectByte);

    public static final GetterSetter IOREGISTER = new GetterSetter(MemoryBus.Bank::getDirectByte, (bank, address, value) -> {
        System.out.println();
        System.out.println("SETTING IO REGISTER ADDRESS: "+ Utils.intToString(address)+" to value: "+Utils.intToString(value));
        bank.setDirectByte(address,value);
        System.out.println(bank.toByteString());
    });
}
