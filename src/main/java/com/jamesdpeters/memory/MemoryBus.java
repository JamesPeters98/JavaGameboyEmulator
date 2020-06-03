package com.jamesdpeters.memory;

import com.jamesdpeters.Utils;

import java.util.Arrays;

public class MemoryBus {

    public static boolean isBootRomEnabled = true;

    public enum Bank {
        BOOT_ROM(0x00,0xFF),
        ROM(0x00, 0x7FFF),
        WORKING_RAM(0xC000, 0xDFFF),
        TILE_RAM(0x8000,0x97FF, GetterSetter.VRAM),
        BACKGROUND_MAP(0x9800,0x9FFF),
        IO_REGISTERS(0xFF00, 0xFF7F, GetterSetter.IOREGISTER),
        HIGH_RAM(0xFF80, 0xFFFE),
        INTERRUPT(0xFFFF, 0xFFFF);

        private int[] memory;
        private final int startAddress, endAddress;
        private GetterSetter getterSetter;
        Bank(int startAddress, int endAddress, GetterSetter getterSetter){
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.getterSetter = getterSetter;
            memory = new  int[endAddress-startAddress+1];
            System.out.println("Creating new Memory: "+toString()+" of size: "+memory.length+" bytes");
        }

        Bank(int startAddress, int endAddress){
            this(startAddress,endAddress,GetterSetter.DEFAULT);
        }

        boolean inRange(int address){
            return (address >= startAddress) && (address <= endAddress);
        }

        int get(int address){
            return getterSetter.get(this, index(address));
        }

        void set(int address, int value){
            getterSetter.set(this, index(address), value);
        }

        int index(int address){
            return address-startAddress;
        }

        int[] toArray(){
            return memory;
        }

        void setMemory(int[] memory){
            System.arraycopy(memory,0,this.memory,0,memory.length);
        }

        int getDirectByte(int address){
//            try {
                return memory[address];
//            } catch (ArrayIndexOutOfBoundsException e){
//                System.out.println();
//                System.err.println(toString()+" out of bounds: "+ Utils.intToString(address));
//                System.exit(-1);
//            }
//            return 0;
        }
        void setDirectByte(int address, int value){
            memory[address] = value;
        }

        public String toByteString() {
            StringBuilder builder = new StringBuilder();
            for(int b : memory){
                builder.append(Integer.toHexString(b)).append(" ");
            }
            return builder.toString();
        }

        /*
                STATIC HELPER METHODS
                 */
        static Bank getMemory(int address){
            if(isBootRomEnabled && address <= BOOT_ROM.endAddress) return BOOT_ROM;
                for(Bank bank : Bank.values()){
                if(!isBootRomEnabled && bank == BOOT_ROM) continue;
                if(bank.inRange(address)) return bank;
            }
            throw new ArrayIndexOutOfBoundsException("No Memory Bank Implemented For Address: 0x"+Integer.toHexString(address));
        }

        static int getByte(int address){
            return getMemory(address).get(address);
        }

        static void setByte(int address, int value){
            getMemory(address).set(address, value);
        }
    }

    public MemoryBus(int[] gameROM){
        System.out.println("ROM SIZE: "+gameROM.length);
        Bank.ROM.setMemory(gameROM);
    }

    public int getByte(int address){
        return Bank.getByte(address);
    }

    public int[] getBytes(Bank bank, int start, int end){
        return Arrays.copyOfRange(bank.toArray(), start, end);
    }

    public void writeByte(int address, int value){
        Bank.setByte(address & 0xFFFF, value);
    }
}
