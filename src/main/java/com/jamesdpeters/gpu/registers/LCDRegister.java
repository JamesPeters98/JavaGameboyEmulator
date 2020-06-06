package com.jamesdpeters.gpu.registers;

import com.jamesdpeters.Utils;
import com.jamesdpeters.memory.MemoryBus;

public abstract class LCDRegister {

    boolean[] bits = new boolean[8];

    public void setByte(int register){
        for(int b=0; b<8; b++){
            bits[b] = Utils.getBit(register,b) == 1;
        }
    }

    abstract int getAddress();

    /**
     * Must be called after updating a flag.
     * Pushes the register to memory.
     */
    void pushRegister(){
        int byte_ = 0;
        for(int b=0; b<8; b++){
            byte_ = Utils.setBit(byte_,b,bits[b]);
        }
        MemoryBus.writeByte(getAddress(),byte_);
    }

}
