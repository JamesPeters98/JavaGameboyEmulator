package com.jamesdpeters.cpu.enums;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.cpu.FlagsRegister;

public enum Instruction {

    NOP((cpu, target) -> {
        return cpu.getRegisters().pc++;
    }),

    HALT((cpu, instruction) -> {
        System.out.println("HALTED!");
        cpu.setState(CPU.State.HALT);
        return cpu.getRegisters().pc;
    }),

    LD((cpu, instruction) -> {
        RegisterBank source = instruction.getLoadType().source;
        RegisterBank target = instruction.getLoadType().target;
        target.setValue(cpu,source.getValue(cpu) & 0xFF);
        return cpu.getRegisters().pc++;
    }),

    //Store value from source into target byte
    LD_R16_A((cpu, instruction) -> {
        int source = instruction.getLoadType().source.getValue(cpu);
        instruction.getLoadType().target.setValue(cpu,source);
        return cpu.getRegisters().pc++;
    }),

    LDH((cpu, instruction) -> {
        int n = cpu.readNextByte();
        int address = (0xFF << 8 | n) & 0xffff;
        cpu.getMemory().writeByte(address, (byte) instruction.getLoadType().target.getValue(cpu));
        return cpu.getRegisters().pc++;
    }),

    LDH_A((cpu, instruction) -> {
        int n = cpu.readNextByte();
        int address = (0xFF << 8 | n) & 0xffff;
        cpu.getMemory().writeByte(address, (byte) instruction.getLoadType().target.getValue(cpu));
        return cpu.getRegisters().pc++;
    }),

    RRC((cpu, instruction) -> {
        int targetValue = instruction.getLoadType().target.getValue(cpu);
        int result = targetValue >> 1;
        if((targetValue & 1) == 1){
            result |= (1 << 7);
            cpu.getRegisters().getF().CARRY = true;
        } else {
            cpu.getRegisters().getF().CARRY = false;
        }
        cpu.getRegisters().getF().ZERO = (result == 0);
        cpu.getRegisters().getF().HALF_CARRY = false;
        cpu.getRegisters().getF().SUBTRACT = false;

        return cpu.getRegisters().pc++;
    }),

    RR((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().target.getValue(cpu);
        int result = targetValue >> 1;
        result |= flags.CARRY ? (1 << 7) : 0;

        flags.CARRY = (targetValue & 1) != 0;
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;

        return cpu.getRegisters().pc++;
    }),

//    LD_RR_NN((cpu, instruction) -> {
//        RegisterBank target = instruction.getLoadType().target;
//        cpu.getRegisters().pc++;
//        int lsb = cpu.readCurrentByte();
//        cpu.getRegisters().pc++;
//        int msb = cpu.readCurrentByte();
//        int value = (lsb << 8 | msb);
//        target.setValue(cpu,value);
//        return cpu.getRegisters().pc++;
//    }),

    INC((cpu, instruction) -> {
        int value = instruction.registerBank.getValue(cpu);
        int result = (value+1) & 0xff;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().HALF_CARRY = (value & 0x0f) == 0x0;
        instruction.registerBank.setValue(cpu, result);
        return cpu.getRegisters().pc++;
    }),

    DEC((cpu, instruction) -> {
        int value = instruction.registerBank.getValue(cpu);
        cpu.getRegisters().getF().ZERO = (value-1) == 0;
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().HALF_CARRY = (1 & 0xF) + (value & 0xF) > 0xF;
        instruction.registerBank.setValue(cpu, value+1);
        return cpu.getRegisters().pc++;
    }),

    JP_N16((cpu, instruction) -> {
        if(instruction.getJumpOptions().isMet(cpu)) {
            cpu.getRegisters().pc++;
            byte lsb = cpu.getMemory().getByte(cpu.getRegisters().pc);
            cpu.getRegisters().pc++;
            byte msb = cpu.getMemory().getByte(cpu.getRegisters().pc);
            cpu.getRegisters().pc = (short) (msb << 8 | lsb);
        } else {
            cpu.getRegisters().pc = (short) (cpu.getRegisters().pc+3);
        }
        return cpu.getRegisters().pc;
    }),

    JR((cpu, instruction) -> {
        if(instruction.getJumpOptions().isMet(cpu)) {
            cpu.getRegisters().pc++;
            byte e = cpu.getMemory().getByte(cpu.getRegisters().pc);
            cpu.getRegisters().pc = (short) ((cpu.getRegisters().pc+e) & 0xffff);
        } else {
            cpu.getRegisters().pc = (short) (cpu.getRegisters().pc+3);
        }
        return cpu.getRegisters().pc;
    }),

    ADD((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte1+byte2;
        cpu.getRegisters().getF().ZERO = (result) == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = (result) > 0xff;
        cpu.getRegisters().getF().HALF_CARRY = (byte1 & 0xF) + (byte2 & 0xF) > 0xF;
        cpu.getRegisters().setA((result) & 0xff);
        return cpu.getRegisters().pc++;
    }),

    SUB((cpu, instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte2-byte1;
        cpu.getRegisters().getF().ZERO = (result) == 0;
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().CARRY = (byte1 > byte2);
        cpu.getRegisters().getF().HALF_CARRY = (0x0f & byte1) > (0x0f & byte2);
        cpu.getRegisters().setA(result & 0xff);
        return cpu.getRegisters().pc++;
    }),

    AND((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte1 & byte2;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = true;
        cpu.getRegisters().getF().HALF_CARRY = false;

        cpu.getRegisters().setA((result));
        return cpu.getRegisters().pc++;
    }),

    OR((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte1 ^ byte2;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = false;
        cpu.getRegisters().getF().HALF_CARRY = false;

        cpu.getRegisters().setA((result));
        return cpu.getRegisters().pc++;
    }),

    XOR((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = (byte1 ^ byte2) & 0xff;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = false;
        cpu.getRegisters().getF().HALF_CARRY = false;

        cpu.getRegisters().setA((result));
        return cpu.getRegisters().pc++;
    });



    //Vars
    InstructionRunnable runnable;
    Instruction(InstructionRunnable runnable){
        this.runnable = runnable;
    }

    private RegisterBank registerBank;
    public RegisterBank getRegisterBank() {return registerBank;}
    public Instruction setTarget(RegisterBank registerBank){
        this.registerBank = registerBank;
        return this;
    }

    private JumpOptions jumpOptions;
    public JumpOptions getJumpOptions() {return jumpOptions;}
    public Instruction setJumpOptions(JumpOptions options){
        this.jumpOptions = options;
        return this;
    }

    private LoadType loadType;
    public LoadType getLoadType() {
        return loadType;
    }
    public Instruction setLoadType(RegisterBank source, RegisterBank target){
        loadType = new LoadType(source, target);
        return this;
    }

    public interface InstructionRunnable {
        int run(CPU cpu, Instruction instruction);
    }

    //Run instruction on given CPU.
    public int run(CPU cpu){
        System.out.println("Running: "+toString());
        return runnable.run(cpu, this);
    }
}


