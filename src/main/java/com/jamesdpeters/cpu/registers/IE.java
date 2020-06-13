package com.jamesdpeters.cpu.registers;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.registers.ByteRegister;

public class IE extends ByteRegister {

    private static IE INSTANCE = new IE();

    public static void set(int byte_){
//        System.out.println("Interrupt Added! ");
//        System.out.println(Utils.intToBinaryString(byte_));
////        System.out.println(GameBoy.getCpu().getRegisters());
//        Utils.waitForInput();
        INSTANCE.setByte(byte_);
    }

    public static void setBit(int bit, boolean bool){
        INSTANCE.bits[bit] = bool;
        INSTANCE.pushRegister();
    }

    public static boolean getBit(int bit){
        return INSTANCE.bits[bit];
    }

    @Override
    public int getAddress() {
        return 0xFFFF;
    }

    public static IE getINSTANCE() {
        return INSTANCE;
    }
}
