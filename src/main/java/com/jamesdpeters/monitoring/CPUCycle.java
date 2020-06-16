package com.jamesdpeters.monitoring;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.*;
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
    private FlagsRegister flags;

    public CPUCycle(CPU cpu){
        this.cpu = cpu;
    }

    public Registers getRegister() {
        return registerCopy;
    }

    public FlagsRegister getFlags() {
        return flags;
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
        cycleAmount = instruction.run(cpu);
        instructionName = instruction.getInstructionName();

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

        //Tick DMA Transfer
        DMATransfer.tick(cycleAmount);

        //Add total cycles from Interrupt
        int interruptCycles = Interrupts.check(cpu);

        //Tick DMA again after interrupts
        if(interruptCycles > 0) DMATransfer.tick(interruptCycles);

        cpu.getRegisters().totalCycles += cycleAmount+interruptCycles;

        if(cpu.scheduleIME){
            cpu.scheduleIME = false;
            cpu.getRegisters().IME = 1;
        }

        try {
            registerCopy = (Registers) cpu.getRegisters().clone();
            flags = (FlagsRegister) cpu.getRegisters().getF().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


        return cycleAmount;
    }
}
