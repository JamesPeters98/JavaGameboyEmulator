package com.jamesdpeters.cpu;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.enums.RegisterBank;
import com.jamesdpeters.cpu.registers.IE;
import com.jamesdpeters.cpu.registers.IF;
import com.jamesdpeters.exceptions.UnknownInstructionException;
import com.jamesdpeters.exceptions.UnknownPrefixInstructionException;
import com.jamesdpeters.monitoring.CPUCycle;

public enum Interrupts {

    // ORDER OF PRIORITY
    V_BLANK(0, 0x40),
    LCD_STAT(1, 0x48),
    TIMER(2, 0x50),
    SERIAL(3, 0x58),
    JOYPAD(4, 0x60);

    private int bit;
    private int address;

    /**
     * @param bit - The bit for the given Interrupt.
     * @param address - The address this interrupt jumps to.
     */
    Interrupts(int bit, int address){
        this.bit = bit;
        this.address = address;
    }

    public void enable(){
        IE.setBit(bit, true);
    }

    public void disable(){
        IE.setBit(bit, false);
    }

    public void request(){
        IF.setBit(bit, true);
    }

    public void removeRequest(){
        IF.setBit(bit, false);
    }

    public boolean isEnabled(){
        return IE.getBit(bit);
    }

    public boolean isRequested(){
        return IF.getBit(bit);
    }

    public static int check(CPU cpu) throws UnknownInstructionException, UnknownPrefixInstructionException {
        if(cpu.getRegisters().IME == 1){
            //Interrupts are enabled.
            for(Interrupts interrupt : values()){
                if(interrupt.isEnabled() && interrupt.isRequested()){
                    interrupt.removeRequest();

                    RegisterBank.SP_DATA.setValue(cpu,cpu.getRegisters().pc & 0xFFFF);
                    cpu.getRegisters().pc = interrupt.address & 0xFF;
                    cpu.getRegisters().IME = 0;

                    CPUCycle cycle = new CPUCycle(cpu);
                    int cycles = cycle.run();
                    return CPU.CPUCYCLE_5 + cycles;
                }
            }
        }
        return 0;
    }


}
