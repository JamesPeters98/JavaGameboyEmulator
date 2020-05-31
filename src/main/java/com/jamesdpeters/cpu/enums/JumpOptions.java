package com.jamesdpeters.cpu.enums;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.cpu.Registers;

public enum JumpOptions {
    ALWAYS(registers -> true),              //Always
    NZ(registers -> !registers.getF().ZERO),     //NotZero
    NC(registers -> !registers.getF().CARRY),    //NotCarry
    Z(registers -> registers.getF().ZERO),       //Zero
    C(registers -> registers.getF().CARRY);      //Carry

    OptionRunnable runnable;
    JumpOptions(OptionRunnable runnable){
        this.runnable = runnable;
    }

    boolean isMet(Registers registers){
        return runnable.run(registers);
    }

    boolean isMet(CPU cpu){
        return isMet(cpu.getRegisters());
    }

    interface OptionRunnable {
        boolean run(Registers registers);
    }

}
