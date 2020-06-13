package com.jamesdpeters.cpu.enums;

import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.memory.MemoryBus;

public enum RegisterBank {
    //Registers
    A("A",cpu -> cpu.getRegisters().getA(), (cpu, value) -> cpu.getRegisters().setA(value)),
    B("B",cpu -> cpu.getRegisters().getB(), (cpu, value) -> cpu.getRegisters().setB(value)),
    C("C",cpu -> cpu.getRegisters().getC(), (cpu, value) -> cpu.getRegisters().setC(value)),
    D("D",cpu -> cpu.getRegisters().getD(), (cpu, value) -> cpu.getRegisters().setD(value)),
    E("E",cpu -> cpu.getRegisters().getE(), (cpu, value) -> cpu.getRegisters().setE(value)),
    H("H",cpu -> cpu.getRegisters().getH(), (cpu, value) -> cpu.getRegisters().setH(value)),
    L("L",cpu -> cpu.getRegisters().getL(), (cpu, value) -> cpu.getRegisters().setL(value)),
    AF("AF",cpu -> cpu.getRegisters().getAF(), (cpu, value) -> cpu.getRegisters().setAF(value)),
    BC("BC",cpu -> cpu.getRegisters().getBC(), (cpu, value) -> cpu.getRegisters().setBC(value)),
    DE("DE",cpu -> cpu.getRegisters().getDE(), (cpu, value) -> cpu.getRegisters().setDE(value)),
    HL("HL",cpu -> cpu.getRegisters().getHL(), (cpu, value) -> cpu.getRegisters().setHL(value)),

    SP("SP",cpu -> cpu.getRegisters().sp, (cpu, value) -> cpu.getRegisters().sp = (short) value),
    D8("d8",CPU::readNextByte,(cpu, value) -> {}),
    D16("d16",CPU::readNextD16,(cpu, value) -> {}),

    SP_DATA("", cpu -> {
        int lsb = MemoryBus.getByte(cpu.getRegisters().sp++ & 0xFFFF);
        int msb = MemoryBus.getByte(cpu.getRegisters().sp++ & 0xFFFF);
        return (msb << 8 | lsb);
    }, (cpu, value) -> {
        int msb = ((value & 0xFF00) >> 8);
        int lsb = (value & 0xFF);
        MemoryBus.writeByte(--cpu.getRegisters().sp, msb);
        MemoryBus.writeByte(--cpu.getRegisters().sp, lsb);
    }),

    A8("(a8)",cpu -> {
        int n = cpu.readNextByte();
        int address = (0xFF << 8 | n) & 0xffff;
        return MemoryBus.getByte(address);
    },(cpu, value) -> {
        int n = cpu.readNextByte();
        int address = (0xFF << 8 | n) & 0xffff;
        MemoryBus.writeByte(address, value);
    }),

    A16("(a16)",cpu -> {
        int nn = cpu.readNextD16();
        return MemoryBus.getByte(nn);
    },(cpu, value) -> {
        int nn = cpu.readNextD16();
        MemoryBus.writeByte(nn, value);
    }),

    C_POINTER("(C)", cpu -> {
        int c = cpu.getRegisters().getC();
        int address = (0xFF << 8 | c) & 0xffff;
        return MemoryBus.getByte(address);
    }, (cpu, value) -> {
        int c = cpu.getRegisters().getC();
        int address = (0xFF << 8 | c) & 0xffff;
        MemoryBus.writeByte(address,value);
    }),

    BC_POINTER("(BC)",cpu -> MemoryBus.getByte(cpu.getRegisters().getBC()),(cpu, value) -> {MemoryBus.writeByte(cpu.getRegisters().getBC(), value);}),
    DE_POINTER("(DE)",cpu -> MemoryBus.getByte(cpu.getRegisters().getDE()),(cpu, value) -> {MemoryBus.writeByte(cpu.getRegisters().getDE(), value);}),

    /**
     * Read/Write from memory at the address specified by the register HL.
     */
    HLI("(HL)",cpu -> MemoryBus.getByte(cpu.getRegisters().getHL()),(cpu, value) -> {MemoryBus.writeByte(cpu.getRegisters().getHL(), value);}),

    /**
     * Read/Write from memory at the address specified by the register HL and then INCREMENT from HL.
     */
    HLI_PLUS("(HL+)",cpu -> {
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
    HLI_MINUS("(HL-)",cpu -> {
        int val = HLI.getValue(cpu);
        cpu.getRegisters().decrementHL();
        return val;
    }, (cpu, value) -> {
        HLI.setValue(cpu,value);
        cpu.getRegisters().decrementHL();
    });

    private Get getMethod;
    private Set setMethod;
    RegisterBank(String id, Get getMethod, Set setMethod){
        this.getMethod = getMethod;
        this.setMethod = setMethod;
        this.id = id;
    }

    private String id;
    public String getId() {
        return id;
    }

    /**
     * Set the value for the given register bank.
     * @param cpu the CPU.
     * @param value to set.
     */
    public void setValue(CPU cpu, int value){
        setMethod.set(cpu,value);
    }

    /**
     * Returns the value for the given register bank.
     * @param cpu the register bank for the CPU.
     * @return value of the given register.
     */
    public int getValue(CPU cpu){
        return getMethod.get(cpu);
    }

    interface Get { int get(CPU cpu);}
    interface Set { void set(CPU cpu, int value);}
}
