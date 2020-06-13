package com.jamesdpeters.cpu.registers;

import com.jamesdpeters.Utils;
import com.jamesdpeters.memory.MemoryBus;
import com.jamesdpeters.registers.ByteRegister;

public class IF extends ByteRegister {

    private static IF INSTANCE = new IF();

    public static void set(int byte_){
        INSTANCE.setByte(byte_);
    }

    public static void setBit(int bit, boolean bool){
        INSTANCE.bits[bit] = bool;
        INSTANCE.pushRegister();
//        System.out.println("Set request flag: "+ Utils.intToBinaryString(MemoryBus.getByte(getINSTANCE().getAddress())));
    }

    public static boolean getBit(int bit){
        return INSTANCE.bits[bit];
    }

    @Override
    public int getAddress() {
        return 0xFF0F;
    }

    public static IF getINSTANCE() {
        return INSTANCE;
    }
}
