package com.jamesdpeters.memory;

import java.util.Arrays;

public class MemoryBus {

    public enum Bank {
        ROM(0x00, 0x7FFF),
        WORKING_RAM(0xC000, 0xDFFF),
        TILE_RAM(0x8000,0x97FF, GetterSetter.VRAM),
        BACKGROUND_MAP(0x9800,0x9FFF),
        IO_REGISTERS(0xFF00, 0xFF7F),
        HIGH_RAM(0xFF80, 0xFFFE),
        INTERRUPT(0xFFFF, 0xFFFF);

        private byte[] memory;
        private final int startAddress, endAddress;
        private GetterSetter getterSetter;
        Bank(int startAddress, int endAddress, GetterSetter getterSetter){
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.getterSetter = getterSetter;
            memory = new byte[endAddress-startAddress+1];
            System.out.println("Creating new Memory: "+toString()+" of size: "+memory.length+" bytes");
        }

        Bank(int startAddress, int endAddress){
            this(startAddress,endAddress,GetterSetter.DEFAULT);
        }

        boolean inRange(int address){
            return (address >= startAddress) && (address <= endAddress);
        }

        byte get(int address){
            return getterSetter.get(this, index(address));
        }

        void set(int address, byte value){
            getterSetter.set(this, index(address), value);
        }

        int index(int address){
            return address-startAddress;
        }

        byte[] toArray(){
            return memory;
        }

        void setMemory(byte[] memory){
            this.memory = memory;
        }

        byte getDirectByte(int address){
            return memory[address];
        }
        void setDirectByte(int address, byte value){
            memory[address] = value;
        }

        /*
        STATIC HELPER METHODS
         */
        static Bank getMemory(int address){
            for(Bank bank : Bank.values()){
                if(bank.inRange(address)) return bank;
            }
            throw new ArrayIndexOutOfBoundsException("No Memory Bank Implemented For Address: 0x"+Integer.toHexString(address));
        }

        static byte getByte(int address){
            return getMemory(address).get(address);
        }

        static void setByte(int address, byte value){
            getMemory(address).set(address, value);
        }
    }

    public MemoryBus(byte[] gameROM){
        System.out.println("ROM SIZE: "+gameROM.length);
        Bank.ROM.setMemory(gameROM);
    }

    public byte getByte(int address){
        return Bank.getByte(address);
    }

    public byte[] getBytes(Bank bank, int start, int end){
        return Arrays.copyOfRange(bank.toArray(), start, end);
    }

    public void writeByte(int address, byte value){
        Bank.setByte(address, value);
    }
}
