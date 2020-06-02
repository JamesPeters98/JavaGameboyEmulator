package com.jamesdpeters.memory;

import com.jamesdpeters.gpu.Tiles;

public class GetterSetter {

    interface Getter {
        byte get(MemoryBus.Bank bank, int address);
    }

    interface Setter {
        void set(MemoryBus.Bank bank, int address, byte value);
    }

    private Getter getter;
    private Setter setter;

    public byte get(MemoryBus.Bank bank, int address){
        return getter.get(bank,address);
    }

    public void set(MemoryBus.Bank bank, int address, byte value){
        setter.set(bank, address, value);
    }

    public GetterSetter(Getter getter, Setter setter){
        this.getter = getter;
        this.setter = setter;
    }

    public static final GetterSetter VRAM = new GetterSetter(
            MemoryBus.Bank::get,
            (bank, address, value) -> {
                bank.setDirectByte(address,value);

                int index = bank.index(address);
                int normalised = index & 0xFFFE;
                byte b1 = bank.getDirectByte(normalised);
                byte b2 = bank.getDirectByte(normalised+1);

                int tileIndex = index / 16;
                int rowIndex = (index % 16) / 2;

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
}
