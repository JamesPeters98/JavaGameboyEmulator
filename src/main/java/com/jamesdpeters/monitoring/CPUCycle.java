package com.jamesdpeters.monitoring;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.cpu.FlagsRegister;
import com.jamesdpeters.cpu.InstructionBuilder;
import com.jamesdpeters.cpu.Interrupts;
import com.jamesdpeters.cpu.Registers;
import com.jamesdpeters.cpu.enums.Instruction;
import com.jamesdpeters.exceptions.UnknownInstructionException;
import com.jamesdpeters.exceptions.UnknownPrefixInstructionException;
import com.jamesdpeters.memory.MemoryBus;

public class CPUCycle {

//    private int pc;
    private String code;
    private int cycleAmount;
    private String instructionName;
//    private int totalCycles;

    private Registers registerCopy;

    private CPU cpu;

    public CPUCycle(CPU cpu){
        this.cpu = cpu;
    }

    public Registers getRegister() {
        return registerCopy;
    }

    public int getPc() {
        if(registerCopy != null)
        return registerCopy.pc;

        return -1;
    }

    public String getCode() {
        return code;
    }

    public int getCycleAmount() {
        return cycleAmount;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public int getTotalCycles() {
        if(registerCopy != null)
        return registerCopy.totalCycles;

        return -1;
    }

    public int run() throws UnknownInstructionException, UnknownPrefixInstructionException {
        int instructionByte = MemoryBus.getByte(cpu.getRegisters().pc);
        Instruction instruction = InstructionBuilder.fromByteNotPrefixed(cpu,instructionByte);
        instructionName = instruction.getInstructionName();
        cycleAmount = instruction.run(cpu);
        cpu.getRegisters().totalCycles += cycleAmount;

            if(instructionByte == 0xCB){
                int instructionByteNext = MemoryBus.getByte(cpu.getRegisters().pc);
                if(cpu.testedPrefixCodes.contains(instructionByte)){
                    cpu.haveTested = true;
                } else {
                    cpu.haveTested = false;
                    cpu.testedPrefixCodes.add(instructionByte);
                }
                code = "0xCB -> "+ Utils.intToString(instructionByteNext);
            } else {
                if(cpu.testedCodes.contains(instructionByte)){
                    cpu.haveTested = true;
                } else {
                    cpu.haveTested = false;
                    cpu.testedCodes.add(instructionByte);
                }
                code = Utils.intToString(instructionByte);
            }
        if(GameBoy.VERBOSE) System.out.println(" | Next Instruction: " + code + " (" + instruction.getInstructionName() + ") - CPU Cycles: " + cycleAmount);

        //Add total cycles from Interrupt
        cpu.getRegisters().totalCycles += Interrupts.check(cpu);

        if(cpu.scheduleIME){
            cpu.scheduleIME = false;
            cpu.getRegisters().IME = 1;
        }

        try {
            registerCopy = (Registers) cpu.getRegisters().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return cycleAmount;
    }
}
