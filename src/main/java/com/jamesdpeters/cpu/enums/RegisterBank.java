package com.jamesdpeters.cpu.enums;

import com.jamesdpeters.cpu.CPU;

public enum RegisterBank {
    //Registers
    A(cpu -> cpu.getRegisters().getA(), (cpu, value) -> cpu.getRegisters().setA(value)),
    B(cpu -> cpu.getRegisters().getB(), (cpu, value) -> cpu.getRegisters().setB(value)),
    C(cpu -> cpu.getRegisters().getC(), (cpu, value) -> cpu.getRegisters().setC(value)),
    D(cpu -> cpu.getRegisters().getD(), (cpu, value) -> cpu.getRegisters().setD(value)),
    E(cpu -> cpu.getRegisters().getE(), (cpu, value) -> cpu.getRegisters().setE(value)),
    H(cpu -> cpu.getRegisters().getH(), (cpu, value) -> cpu.getRegisters().setH(value)),
    L(cpu -> cpu.getRegisters().getL(), (cpu, value) -> cpu.getRegisters().setL(value)),
    AF(cpu -> cpu.getRegisters().getAF(), (cpu, value) -> cpu.getRegisters().setAF(value)),
    BC(cpu -> cpu.getRegisters().getBC(), (cpu, value) -> cpu.getRegisters().setBC(value)),
    DE(cpu -> cpu.getRegisters().getDE(), (cpu, value) -> cpu.getRegisters().setDE(value)),
    HL(cpu -> cpu.getRegisters().getHL(), (cpu, value) -> cpu.getRegisters().setHL(value)),

    SP(cpu -> cpu.getRegisters().sp, (cpu, value) -> cpu.getRegisters().sp = (short) value),
    D8(CPU::readNextByte,(cpu, value) -> {}),
    D16(CPU::readNextD16,(cpu, value) -> {}),

    BC_POINTER(cpu -> cpu.getMemory().getByte(cpu.getRegisters().getBC()),(cpu, value) -> {cpu.getMemory().writeByte(cpu.getRegisters().getBC(), (byte) value);}),
    DE_POINTER(cpu -> cpu.getMemory().getByte(cpu.getRegisters().getDE()),(cpu, value) -> {cpu.getMemory().writeByte(cpu.getRegisters().getDE(), (byte) value);}),

    /**
     * Read/Write from memory at the address specified by the register HL.
     */
    HLI(cpu -> cpu.getMemory().getByte(cpu.getRegisters().getHL()),(cpu, value) -> {cpu.getMemory().writeByte(cpu.getRegisters().getHL(), (byte) value);}),

    /**
     * Read/Write from memory at the address specified by the register HL and then INCREMENT from HL.
     */
    HLI_PLUS(cpu -> {
        int val = HLI.getValue(cpu);
        cpu.getRegisters().incrementHL();
        return val;
    }, (cpu, value) -> {
        HLI.setValue(cpu,value);
        cpu.getRegisters().incrementHL();
    }),

    /**
     * Read/Write from memory at the address specified by the register HL and then DECREMENT from HL.
     */
    HLI_MINUS(cpu -> {
        int val = HLI.getValue(cpu);
        cpu.getRegisters().incrementHL();
        return val;
    }, (cpu, value) -> {
        HLI.setValue(cpu,value);
        cpu.getRegisters().incrementHL();
    });

    private Get getMethod;
    private Set setMethod;
    RegisterBank(Get getMethod, Set setMethod){
        this.getMethod = getMethod;
        this.setMethod = setMethod;
    }

    /**
     * Set the value for the given register bank.
     * @param cpu the CPU.
     * @param value to set.
     */
    void setValue(CPU cpu, int value){
        setMethod.set(cpu,value);
    }

    /**
     * Returns the value for the given register bank.
     * @param cpu the register bank for the CPU.
     * @return value of the given register.
     */
    int getValue(CPU cpu){
        return getMethod.get(cpu);
    }

    interface Get { int get(CPU cpu);}
    interface Set { void set(CPU cpu, int value);}
}
