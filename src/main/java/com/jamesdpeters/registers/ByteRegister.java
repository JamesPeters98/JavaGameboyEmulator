package com.jamesdpeters.registers;

import com.jamesdpeters.Utils;
import com.jamesdpeters.memory.MemoryBus;

import java.util.Arrays;

public abstract class ByteRegister {

    public boolean[] bits = new boolean[8];

    public void setByte(int register){
        for(int b=0; b<8; b++){
            bits[b] = Utils.getBit(register,b) == 1;
        }
    }

    public abstract int getAddress();

    /**
     * Must be called after updating a flag.
     * Pushes the register to memory.
     */
    public void pushRegister(){
        int byte_ = 0;
        for(int b=0; b<8; b++){
            byte_ = Utils.setBit(byte_,b,bits[b]);
        }
        MemoryBus.writeByteDuringDMA(getAddress(),byte_);
    }

    @Override
    public String toString() {
        return Arrays.toString(bits);
    }
}
