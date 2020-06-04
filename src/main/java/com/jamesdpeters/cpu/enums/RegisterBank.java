package com.jamesdpeters.cpu.enums;

import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.memory.MemoryBus;

public enum RegisterBank {
    //Registers
    A("A",cpu -> cpu.getRegisters().getA(), (cpu, value) -> cpu.getRegisters().setA(value),CPU.CPUCYCLE_1,0),
    B("B",cpu -> cpu.getRegisters().getB(), (cpu, value) -> cpu.getRegisters().setB(value),CPU.CPUCYCLE_1,0),
    C("C",cpu -> cpu.getRegisters().getC(), (cpu, value) -> cpu.getRegisters().setC(value),CPU.CPUCYCLE_1,0),
    D("D",cpu -> cpu.getRegisters().getD(), (cpu, value) -> cpu.getRegisters().setD(value),CPU.CPUCYCLE_1,0),
    E("E",cpu -> cpu.getRegisters().getE(), (cpu, value) -> cpu.getRegisters().setE(value),CPU.CPUCYCLE_1,0),
    H("H",cpu -> cpu.getRegisters().getH(), (cpu, value) -> cpu.getRegisters().setH(value),CPU.CPUCYCLE_1,0),
    L("L",cpu -> cpu.getRegisters().getL(), (cpu, value) -> cpu.getRegisters().setL(value),CPU.CPUCYCLE_1,0),
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
        cpu.getRegisters().sp--;
        MemoryBus.writeByte(cpu.getRegisters().sp, msb);
        cpu.getRegisters().sp--;
        MemoryBus.writeByte(cpu.getRegisters().sp, lsb);
    }),

    A8("(a8)",cpu -> {
        int n = cpu.readNextByte();
        int address = (0xFF << 8 | n) & 0xffff;
        int result = MemoryBus.getByte(address);
        System.out.println();
        System.out.println("n value = "+ Utils.intToString(n));
        System.out.println("adr value = "+ Utils.intToString(address));
        System.out.println("(a8) value = "+ Utils.intToString(result));
        return result;
    },(cpu, value) -> {
        int n = cpu.readNextByte();
        int address = (0xFF << 8 | n) & 0xffff;
        MemoryBus.writeByte(address, value);
    },CPU.CPUCYCLE_2,CPU.CPUCYCLE_2),

    C_POINTER("(C)", cpu -> {
        int c = cpu.getRegisters().getC();
        int address = (0xFF << 8 | c) & 0xffff;
        int result = MemoryBus.getByte(address);
        return result;
    }, (cpu, value) -> {
        int c = cpu.getRegisters().getC();
        int address = (0xFF << 8 | c) & 0xffff;
        MemoryBus.writeByte(address,value);
    }),

    BC_POINTER("(BC)",cpu -> MemoryBus.getByte(cpu.getRegisters().getBC()),(cpu, value) -> {MemoryBus.writeByte(cpu.getRegisters().getBC(), value);},CPU.CPUCYCLE_1,CPU.CPUCYCLE_1),
    DE_POINTER("(DE)",cpu -> MemoryBus.getByte(cpu.getRegisters().getDE()),(cpu, value) -> {MemoryBus.writeByte(cpu.getRegisters().getDE(), value);},CPU.CPUCYCLE_1,CPU.CPUCYCLE_1),

    /**
     * Read/Write from memory at the address specified by the register HL.
     */
    HLI("(HL)",cpu -> MemoryBus.getByte(cpu.getRegisters().getHL()),(cpu, value) -> {MemoryBus.writeByte(cpu.getRegisters().getHL(), value);},CPU.CPUCYCLE_1,CPU.CPUCYCLE_1),

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
    },CPU.CPUCYCLE_1,CPU.CPUCYCLE_1),

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
    },CPU.CPUCYCLE_1,CPU.CPUCYCLE_1);

    private Get getMethod;
    private Set setMethod;
    RegisterBank(String id, Get getMethod, Set setMethod, int getCPUCycles, int setCPUCycles){
        this.getMethod = getMethod;
        this.setMethod = setMethod;
        this.getCPUCycles = getCPUCycles;
        this.setCPUCycles = setCPUCycles;
        this.id = id;
    }

    RegisterBank(String id, Get getMethod, Set setMethod){
        this(id,getMethod,setMethod,0,0);
    }

    private int getCPUCycles, setCPUCycles;
    private String id;

    public String getId() {
        return id;
    }

    public int getCPUCycles() {
        return getCPUCycles;
    }

    public int getSetCPUCycles(){
        return setCPUCycles;
    }

    public void setGetCPUCycles(int getCPUCycles) {
        this.getCPUCycles = getCPUCycles;
    }

    public void setSetCPUCycles(int setCPUCycles) {
        this.setCPUCycles = setCPUCycles;
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
