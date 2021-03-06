package com.jamesdpeters.memory;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.exceptions.DMATransferException;

import java.util.Arrays;

public class MemoryBus {

    public static boolean isBootRomEnabled = true;
    public static boolean isDMATransferActive = false;

    public enum Bank {
        BOOT_ROM(0x00,0xFF,GetterSetter.DEFAULT,true),
        ROM(0x00, 0x7FFF,GetterSetter.DEFAULT,true),
        CARTRIDGE_RAM(0xA000, 0xBFFF),
        WORKING_RAM(0xC000, 0xDFFF),
        VRAM(0x8000,0x97FF, GetterSetter.VRAM,false),
        BACKGROUND_MAP(0x9800, 0x9FFF),
        //TODO Still Memory Mappings to add here.
        OAM(0xFE00, 0xFE9F, GetterSetter.SPRITE_TABLE, false),
        UNUSEABLE(0xFEA0, 0xFEFF, GetterSetter.UNUSEABLE, false),
        IO_REGISTERS(0xFF00, 0xFF7F, GetterSetter.IOREGISTER,false),
        HIGH_RAM(0xFF80, 0xFFFE, GetterSetter.HRAM, false),
        INTERRUPT(0xFFFF, 0xFFFF, GetterSetter.INTERRUPTS, false);

        private int[] memory;
        private final int startAddress, endAddress;
        private boolean readonly;
        private GetterSetter getterSetter;
        Bank(int startAddress, int endAddress, GetterSetter getterSetter, boolean readonly){
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.getterSetter = getterSetter;
            this.readonly = readonly;
            memory = new  int[endAddress-startAddress+1];
            System.out.println("Creating new Memory: "+toString()+" of size: "+memory.length+" bytes");
        }

        Bank(int startAddress, int endAddress){
            this(startAddress,endAddress,GetterSetter.DEFAULT,false);
        }

        boolean inRange(int address){
            return (address >= startAddress) && (address <= endAddress);
        }

        int get(int address) throws DMATransferException {
            if(isDMATransferActive && this != HIGH_RAM) throw new DMATransferException(GameBoy.getCpu().getRegisters().pc);
            return getDuringDMA(address);
        }

        int getDuringDMA(int address){
            return getterSetter.get(this, index(address));
        }

        void set(int address, int value) throws DMATransferException {
            if(isDMATransferActive && this != HIGH_RAM) throw new DMATransferException(GameBoy.getCpu().getRegisters().pc);
            setDuringDMA(address, value);
        }

        void setDuringDMA(int address, int value){
            if(readonly){
                //Tetris actually tries to write to ROM so just ignore writes to ROM.
                return;
            }
            getterSetter.set(this, index(address), value);
        }

        int index(int address){
            return address-startAddress;
        }

        int[] toArray(){
            return memory;
        }

        void setMemory(int[] memory){
            System.arraycopy(
                    memory,
                    0,
                    this.memory,
                    0,
                    memory
                            .length);
        }

        public int getDirectByte(int address){
                return memory[address];
        }
        public void setDirectByte(int address, int value){
            memory[address] = value;
        }

        public String toByteString() {
            StringBuilder builder = new StringBuilder();
            for(int b : memory){
                builder.append(Utils.intToString(b)).append(" ");
            }
            return builder.toString();
        }

        public byte[] toBytes(){
            byte[] bytes = new byte[memory.length];
            for(int i=0; i<memory.length; i++){
                bytes[i] = (byte) memory[i];
            }
            return bytes;
        }

        public int getStartAddress(){
            return startAddress;
        }

        public int getEndAddress() {
            return endAddress;
        }

        /*
                    STATIC HELPER METHODS
                 */
        static Bank getMemory(int address){

            if(isBootRomEnabled && address <= BOOT_ROM.endAddress){
                return BOOT_ROM;
            }
                for(Bank bank : Bank.values()){
                    if(!isBootRomEnabled && bank == BOOT_ROM) continue;
                    if(bank.inRange(address)){
                        return bank;
                    }
                }
                System.out.println(GameBoy.getCpu().getRegisters());
            throw new ArrayIndexOutOfBoundsException("No Memory Bank Implemented For Address: 0x"+Integer.toHexString(address));
        }

        static int getByte(int address){
            try {
                return getMemory(address).get(address);
            } catch (DMATransferException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            return -1;
        }

        static int getByteDuringDMA(int address){
            return getMemory(address).getDuringDMA(address);
        }

        static void setByte(int address, int value){
            try {
                getMemory(address).set(address, value);
            } catch (DMATransferException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        static void setByteDuringDMA(int address, int value){
            getMemory(address).setDuringDMA(address, value);
        }
    }

    public static void setROM(int[] gameROM){
        System.out.println("ROM SIZE: "+gameROM.length);
        Bank.ROM.setMemory(gameROM);
    }

    public static void setBootROM(int[] bootROM){
        Bank.BOOT_ROM.setMemory(bootROM);
    }

    public static int getByte(int address){
        return Bank.getByte(address);
    }

    public static int getByteDuringDMA(int address){ return Bank.getByteDuringDMA(address); }

    public static int[] getBytes(Bank bank, int start, int end){
        return Arrays.copyOfRange(bank.toArray(), start, end);
    }

    public static void writeByte(int address, int value){
        Bank.setByte(address & 0xFFFF, value);
    }

    public static void writeByteDuringDMA(int address, int value){
        Bank.setByteDuringDMA(address, value);
    }
}
