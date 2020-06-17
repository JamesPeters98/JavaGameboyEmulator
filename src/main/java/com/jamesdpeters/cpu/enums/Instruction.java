package com.jamesdpeters.cpu.enums;

import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.cpu.FlagsRegister;
import com.jamesdpeters.memory.MemoryBus;
import com.jamesdpeters.monitoring.CPUCycle;

public enum Instruction {

    NOP((cpu, instruction) -> {
        instruction.setInstructionName("NOP");
        cpu.getRegisters().pc++;
//        return 0;
        return CPU.CPUCYCLE_1;
    }),

    HALT((cpu, instruction) -> {
        instruction.setInstructionName("HALT");
        System.out.println("HALTED!");
        cpu.setState(CPU.State.HALT);
        return CPU.CPUCYCLE_1;
    }),

    DI((cpu, instruction) -> {
        instruction.setInstructionName("DI");
        cpu.getRegisters().IME = 0;
        cpu.getRegisters().pc++;
        return CPU.CPUCYCLE_1;
    }),

    EI((cpu, instruction) -> {
        instruction.setInstructionName("EI");
        cpu.scheduleIME = true;
        cpu.getRegisters().pc++;
        return CPU.CPUCYCLE_1;
    }),

    DAA((cpu, instruction) -> {
        int result = RegisterBank.A.getValue(cpu);
        FlagsRegister flags = cpu.getRegisters().getF();

        if(flags.SUBTRACT){
            if(flags.HALF_CARRY){
                result = (result - 6) & 0xFF;
            }
            if(flags.CARRY){
                result = (result - 0x60) & 0xFF;
            }
        } else {
            if(flags.HALF_CARRY || (result & 0xf) > 9){
                result += 0x06;
            }
            if(flags.CARRY || result > 0x9f){
                result += 0x60;
            }
        }

        flags.HALF_CARRY = false;
        if(result > 0xff){
            flags.CARRY = true;
        }
        result &= 0xff;
        flags.ZERO = result == 0;

        RegisterBank.A.setValue(cpu,result);

        return CPU.CPUCYCLE_1;
    }),

    LD((cpu, instruction) -> {
        RegisterBank source = instruction.getLoadType().source;
        RegisterBank target = instruction.getLoadType().target;
        int sourceVal = source.getValue(cpu) & 0xFF;
        target.setValue(cpu,sourceVal);
        cpu.getRegisters().pc++;

        instruction.setInstructionName("LD "+target.getId()+" "+source.getId() +" ["+Utils.intToString(sourceVal)+"]");
        int cpuCycles = CPU.CPUCYCLE_1;
        if(source == RegisterBank.HLI || source == RegisterBank.D8 || source == RegisterBank.C_POINTER) cpuCycles += CPU.CPUCYCLE_1;
        if(target == RegisterBank.HLI || target == RegisterBank.D8 || target == RegisterBank.C_POINTER) cpuCycles += CPU.CPUCYCLE_1;
        if(source == RegisterBank.A16 || target == RegisterBank.A16) cpuCycles += CPU.CPUCYCLE_3;
        return cpuCycles;
    }),

    LD_16((cpu, instruction) -> {
        RegisterBank source = instruction.getLoadType().source;
        RegisterBank target = instruction.getLoadType().target;
        int sourceVal = source.getValue(cpu) & 0xFFFF;
        target.setValue(cpu,sourceVal);
        instruction.setInstructionName("LD "+target.getId()+" "+source.getId()+" ["+Utils.intToString(sourceVal)+"]");
        cpu.getRegisters().pc++;
        if(target == RegisterBank.A16 || source == RegisterBank.A16) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_3;
    }),

    //Store value from source into target byte
    LD_R16_A((cpu, instruction) -> {
        RegisterBank source = instruction.getLoadType().source;
        RegisterBank target = instruction.getLoadType().target;
        int sourceVal = source.getValue(cpu) & 0xFF;
        target.setValue(cpu,sourceVal);
        instruction.setInstructionName("LD "+target.getId()+" "+source.getId()+" ["+Utils.intToString(sourceVal)+"]");
        cpu.getRegisters().pc++;
        return CPU.CPUCYCLE_2;
    }),

    LDH((cpu, instruction) -> {
        instruction.getLoadType().target.setValue(cpu,instruction.getLoadType().source.getValue(cpu));
        instruction.setInstructionName("LDH "+instruction.getLoadType().target.getId()+" "+instruction.getLoadType().source.getId());
        cpu.getRegisters().pc++;
        return CPU.CPUCYCLE_3;
    }),

    CP((cpu, instruction) -> {
        int b1 = instruction.getLoadType().target.getValue(cpu);
        int b2 = instruction.getLoadType().source.getValue(cpu);
        int result = (b1-b2) & 0xFF;
        cpu.getRegisters().getF().ZERO = (result) == 0;
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().CARRY = b2 > b1;
        cpu.getRegisters().getF().HALF_CARRY = (0x0f & b2) > (0x0f & b1);
        instruction.setInstructionName("CP "+instruction.getLoadType().target.getId()+" "+instruction.getLoadType().source.getId()+ " ["+Utils.intToString(b2)+"]");

        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    RRC((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result = targetValue >> 1;
        if((targetValue & 1) == 1){
            result |= (1 << 7);
            flags.CARRY = true;
        } else {
            flags.CARRY = false;
        }
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;

        instruction.getLoadType().source.setValue(cpu,result);

        if(instruction.isClearZ()){
            flags.ZERO = false;
            instruction.setInstructionName("RRC"+instruction.getLoadType().source.getId());
        } else {
            instruction.setInstructionName("RRC "+instruction.getLoadType().source.getId());
        }
        cpu.getRegisters().pc++;

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    RR((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result = targetValue >> 1;
        result |= flags.CARRY ? (1 << 7) : 0;

        flags.CARRY = (targetValue & 1) != 0;
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;
        cpu.getRegisters().pc++;

        instruction.getLoadType().source.setValue(cpu,result);

        if(instruction.isClearZ()){
            flags.ZERO = false;
            instruction.setInstructionName("RR"+instruction.getLoadType().source.getId());
            return CPU.CPUCYCLE_1;
        } else {
            instruction.setInstructionName("RR "+instruction.getLoadType().source.getId());
            cpu.getRegisters().pc++;
        }

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    RLC((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result = (targetValue << 1) & 0xFF;
        if((targetValue & (1<<7)) != 0){
            result |= 1;
            flags.CARRY = true;
        } else {
            flags.CARRY = false;
        }
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;
        cpu.getRegisters().pc++;
        instruction.getLoadType().source.setValue(cpu,result);

        if(instruction.isClearZ()){
            flags.ZERO = false;
            instruction.setInstructionName("RLC"+instruction.getLoadType().source.getId());
            return CPU.CPUCYCLE_1;
        } else {
            instruction.setInstructionName("RLC "+instruction.getLoadType().source.getId());
            cpu.getRegisters().pc++;
        }

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    RL((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result = (targetValue << 1) & 0xFF;
        result |= flags.CARRY ? 1 : 0;

        flags.CARRY = (targetValue & (1<<7)) != 0;
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;
        cpu.getRegisters().pc++;
        instruction.getLoadType().source.setValue(cpu,result);

        if(instruction.isClearZ()){
            flags.ZERO = false;
            instruction.setInstructionName("RL"+instruction.getLoadType().source.getId());
            return CPU.CPUCYCLE_1;
        } else {
            instruction.setInstructionName("RL "+instruction.getLoadType().source.getId());
            cpu.getRegisters().pc++;
        }

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    SLA((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result = (targetValue << 1) & 0xFF;
        flags.CARRY = (targetValue & (1 << 7)) != 0;
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;

        cpu.getRegisters().pc++;
        instruction.getLoadType().source.setValue(cpu,result);
        instruction.setInstructionName("SLA "+instruction.getLoadType().source.getId());
        cpu.getRegisters().pc++;

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    SRA((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result =  (targetValue >> 1) | (targetValue & (1 << 7));
        flags.CARRY = (targetValue & 1) != 0;
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;

        cpu.getRegisters().pc++;
        instruction.getLoadType().source.setValue(cpu,result);
        instruction.setInstructionName("SRA "+instruction.getLoadType().source.getId());
        cpu.getRegisters().pc++;

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    SRL((cpu, instruction) -> {
        FlagsRegister flags = cpu.getRegisters().getF();
        int targetValue = instruction.getLoadType().source.getValue(cpu);
        int result =  (targetValue >> 1);
        flags.CARRY = (targetValue & 1) != 0;
        flags.ZERO = (result == 0);
        flags.HALF_CARRY = false;
        flags.SUBTRACT = false;

        cpu.getRegisters().pc++;
        instruction.getLoadType().source.setValue(cpu,result);
        instruction.setInstructionName("SRL "+instruction.getLoadType().source.getId());
        cpu.getRegisters().pc++;

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    SET((cpu, instruction) -> {
        int bit = instruction.getBit();
        RegisterBank source = instruction.getLoadType().source;
        int b = source.getValue(cpu) | 1 << bit;
        source.setValue(cpu,b);
        cpu.getRegisters().pc++;
        cpu.getRegisters().pc++;
        instruction.setInstructionName("SET "+bit+","+source);

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    BIT((cpu, instruction) -> {
        int bit = instruction.getBit();
        RegisterBank source = instruction.getLoadType().source;
        int result = Utils.getBit(source.getValue(cpu),bit);

        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().HALF_CARRY = true;
        if(bit < 8) cpu.getRegisters().getF().ZERO = (result == 0);

        instruction.setInstructionName("BIT "+bit+","+source);
        cpu.getRegisters().pc++;
        cpu.getRegisters().pc++;

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    RES((cpu, instruction) -> {
        int bit = instruction.getBit();
        RegisterBank source = instruction.getLoadType().source;
        int sourceVal = source.getValue(cpu) & 0xFF;
        int result = Utils.setBit(sourceVal,bit,false);
        source.setValue(cpu,result);
        instruction.setInstructionName("RES "+bit+" "+source);
        cpu.getRegisters().pc++;
        cpu.getRegisters().pc++;

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    SWAP((cpu, instruction) -> {
        int value = instruction.getLoadType().source.getValue(cpu);
        int upper = (value & 0xf0) >> 4;
        int lower = (value & 0x0f) << 4;
        int swapped = upper+lower;
        instruction.getLoadType().source.setValue(cpu,swapped);
        cpu.getRegisters().pc++;
        cpu.getRegisters().pc++;
        instruction.setInstructionName("SWAP "+instruction.getLoadType().source.getId());

        if(instruction.getLoadType().source == RegisterBank.HLI) return CPU.CPUCYCLE_4;
        return CPU.CPUCYCLE_2;
    }),

    INC((cpu, instruction) -> {
        int value = instruction.registerBank.getValue(cpu);
        int result = (value+1) & 0xff;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().HALF_CARRY = (value & 0x0f) == 0x0f;
        instruction.registerBank.setValue(cpu, result);
        instruction.setInstructionName("INC "+instruction.registerBank.getId());
        cpu.getRegisters().pc++;

        if(instruction.registerBank == RegisterBank.HLI) return CPU.CPUCYCLE_3;
        return CPU.CPUCYCLE_1;
    }),

    INC_16((cpu, instruction) -> {
        int value = instruction.registerBank.getValue(cpu);
        int result = (value+1) & 0xffff;
        instruction.registerBank.setValue(cpu, result);
        instruction.setInstructionName("INC "+instruction.registerBank.getId());
        cpu.getRegisters().pc++;

        return CPU.CPUCYCLE_2;
    }),

    DEC((cpu, instruction) -> {
        int value = instruction.registerBank.getValue(cpu);
        int result = (value-1) & 0xFF;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().HALF_CARRY = (0x0f & 1) > (0x0f & value);
        instruction.registerBank.setValue(cpu, result);
        instruction.setInstructionName("DEC "+instruction.registerBank.getId());
        cpu.getRegisters().pc++;

        if(instruction.registerBank == RegisterBank.HLI) return CPU.CPUCYCLE_3;
        return CPU.CPUCYCLE_1;
    }),

    DEC_16((cpu, instruction) -> {
        int value = instruction.registerBank.getValue(cpu);
        int result = (value-1) & 0xFFFF;

        instruction.registerBank.setValue(cpu, result);
        instruction.setInstructionName("DEC "+instruction.registerBank.getId());
        cpu.getRegisters().pc++;

        return CPU.CPUCYCLE_2;
    }),

    JP_N16((cpu, instruction) -> {
        instruction.setInstructionName("JP "+instruction.getJumpCondition()+" (a16)");
        if(instruction.getJumpCondition().isMet(cpu)) {
            cpu.getRegisters().pc++;
            int lsb = MemoryBus.getByte(cpu.getRegisters().pc);
            cpu.getRegisters().pc++;
            int msb = MemoryBus.getByte(cpu.getRegisters().pc);
            cpu.getRegisters().pc = (msb << 8 | lsb) & 0xFFFF;

            return CPU.CPUCYCLE_4;
        } else {
            cpu.getRegisters().pc = (cpu.getRegisters().pc+3) & 0xFFFF;
            return CPU.CPUCYCLE_3;
        }
    }),

    JP_HL((cpu, instruction) -> {
        cpu.getRegisters().pc = cpu.getRegisters().getHL();
        return CPU.CPUCYCLE_1;
    }),

    JR((cpu, instruction) -> {
        instruction.setInstructionName("JR "+instruction.getJumpCondition()+" (r8)");
        if(instruction.getJumpCondition().isMet(cpu)) {
            cpu.getRegisters().pc++;
            byte e = (byte) MemoryBus.getByte(cpu.getRegisters().pc);
            cpu.getRegisters().pc++;
            cpu.getRegisters().pc = ((cpu.getRegisters().pc+e) & 0xffff);
            instruction.setInstructionName("JR "+instruction.getJumpCondition()+" (r8) ["+Utils.intToString(cpu.getRegisters().pc)+"]");

            return CPU.CPUCYCLE_3;
        } else {
            cpu.getRegisters().pc = (cpu.getRegisters().pc+2) & 0xffff;
            return CPU.CPUCYCLE_2;
        }
    }),

    RST((cpu, instruction) -> {
        int nn = instruction.getRSTAddress();
        int pc = cpu.getRegisters().pc+1;
        MemoryBus.writeByte(--cpu.getRegisters().sp, (pc & 0xff00) >> 8);
        MemoryBus.writeByte(--cpu.getRegisters().sp, (pc & 0xff));
        cpu.getRegisters().pc = nn;

        instruction.setInstructionName("RST "+Integer.toHexString(nn)+"H");
        return CPU.CPUCYCLE_4;
    }),

    CALL((cpu, instruction) -> {
        instruction.setInstructionName("CALL "+instruction.getJumpCondition()+" (a16)");
        int lsb = MemoryBus.getByte(++cpu.getRegisters().pc);
        int msb = MemoryBus.getByte(++cpu.getRegisters().pc);
        int nn = (msb << 8 | lsb);

        if(instruction.getJumpCondition().isMet(cpu)) {
            RST.setRSTAddress(nn).run(cpu);
            return CPU.CPUCYCLE_6;
        } else {
            cpu.getRegisters().pc++;
            return CPU.CPUCYCLE_3;
        }
    }),

    POP((cpu, instruction) -> {
        instruction.setInstructionName("POP "+instruction.getLoadType().target.getId());
        instruction.getLoadType().target.setValue(cpu, instruction.getLoadType().source.getValue(cpu));
        cpu.getRegisters().pc++;

        return CPU.CPUCYCLE_3;
    }),

    PUSH((cpu, instruction) -> {
        instruction.getLoadType().source.setValue(cpu, instruction.getLoadType().target.getValue(cpu));
        cpu.getRegisters().pc++;
        instruction.setInstructionName("PUSH "+instruction.getLoadType().target.getId());

        return CPU.CPUCYCLE_4;
    }),

    RET((cpu, instruction) -> {
        if(instruction.getJumpCondition().isMet(cpu)){
            cpu.getRegisters().pc = RegisterBank.SP_DATA.getValue(cpu);
            instruction.setInstructionName("RET "+instruction.getJumpCondition());
            if(instruction.getJumpCondition() == JumpCondition.ALWAYS_IME){
                cpu.getRegisters().IME = 1;
                instruction.setInstructionName("RETI");
            }
            if(instruction.getJumpCondition() == JumpCondition.ALWAYS){
                instruction.setInstructionName("RET");
                return CPU.CPUCYCLE_4;
            }
            return CPU.CPUCYCLE_5;
        } else {
            cpu.getRegisters().pc++;
            return CPU.CPUCYCLE_2;
        }
    }),

    CPL((cpu, instruction) -> {
        cpu.getRegisters().setA(~cpu.getRegisters().getA() & 0xFF);
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().HALF_CARRY = true;
        cpu.getRegisters().pc++;
        instruction.setInstructionName("CPL");
        return CPU.CPUCYCLE_1;
    }),

//    PUSH((cpu, instruction) -> {
//        int register = instruction.getRegisterBank().getValue(cpu);
//        int msb = ((register & 0xFF00) >> 8);
//        int lsb = (register & 0xFF);
//        MemoryBus.writeByte(--cpu.getRegisters().sp, msb);
//        MemoryBus.writeByte(--cpu.getRegisters().sp, lsb);
//        cpu.getRegisters().pc++;
//
//        instruction.setInstructionName("PUSH "+instruction.getRegisterBank().getId());
//        return CPU.CPUCYCLE_1;
//    }),

    ADD((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = (byte1+byte2);
        cpu.getRegisters().getF().ZERO = (result & 0xff) == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = (result) > 0xff;
        cpu.getRegisters().getF().HALF_CARRY = (byte1 & 0xF) + (byte2 & 0xF) > 0xF;
        cpu.getRegisters().setA((result) & 0xff);
        instruction.setInstructionName("ADD "+instruction.getLoadType().target+" "+instruction.getLoadType().source);
        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    ADD_HL((cpu,instruction) -> {
        int n8 = instruction.getRegisterBank().getValue(cpu);
        int HL = cpu.getRegisters().getHL();
        int result = (n8 + HL);
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = (result) > 0xffff;
        cpu.getRegisters().getF().HALF_CARRY = (n8 & 0xFF) + (HL & 0xFF) > 0xFF;
        cpu.getRegisters().setHL((result) & 0xffff);
        instruction.setInstructionName("ADD HL "+instruction.getRegisterBank());
        cpu.getRegisters().pc++;
        return CPU.CPUCYCLE_2;
    }),

    ADC((cpu,instruction) -> {
        int carry = cpu.getRegisters().getF().CARRY ? 1 : 0;
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = (byte1+byte2+carry);
        cpu.getRegisters().getF().ZERO = (result & 0xff) == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = (result) > 0xff;
        cpu.getRegisters().getF().HALF_CARRY = (byte1 & 0xF) + (byte2 & 0xF) + carry > 0xF;
        cpu.getRegisters().setA((result) & 0xff);
        instruction.setInstructionName("ADC "+instruction.getLoadType().target+" "+instruction.getLoadType().source);
        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    SUB((cpu, instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte2-byte1;
        cpu.getRegisters().getF().ZERO = (result & 0xFF) == 0;
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().CARRY = (byte1 > byte2);
        cpu.getRegisters().getF().HALF_CARRY = (0x0f & byte1) > (0x0f & byte2);
        cpu.getRegisters().setA(result & 0xff);
        instruction.setInstructionName("SUB "+instruction.getLoadType().target+" "+instruction.getLoadType().source);
        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    SBC((cpu,instruction) -> {
        int carry = cpu.getRegisters().getF().CARRY ? 1 : 0;
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte2-byte1-carry;
        cpu.getRegisters().getF().ZERO = (result) == 0;
        cpu.getRegisters().getF().SUBTRACT = true;
        cpu.getRegisters().getF().CARRY = result < 0;
        cpu.getRegisters().getF().HALF_CARRY = ((byte2 ^ byte1 ^ (result & 0xff)) & (1 << 4)) != 0;
        cpu.getRegisters().setA(result & 0xff);
        instruction.setInstructionName("SBC "+instruction.getLoadType().target+" "+instruction.getLoadType().source);
        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    AND((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte1 & byte2;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = false;
        cpu.getRegisters().getF().HALF_CARRY = true;

        cpu.getRegisters().setA((result));
        instruction.setInstructionName("AND "+instruction.getLoadType().target+" "+instruction.getLoadType().source);

        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    }),

    OR((cpu,instruction) -> {
        int byte1 = instruction.getLoadType().source.getValue(cpu);
        int byte2 = instruction.getLoadType().target.getValue(cpu);
        int result = byte1 | byte2;
        cpu.getRegisters().getF().ZERO = result == 0;
        cpu.getRegisters().getF().SUBTRACT = false;
        cpu.getRegisters().getF().CARRY = false;
        cpu.getRegisters().getF().HALF_CARRY = false;

        cpu.getRegisters().setA((result));
        instruction.setInstructionName("OR "+instruction.getLoadType().target+" "+instruction.getLoadType().source);

        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
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
        instruction.setInstructionName("XOR "+instruction.getLoadType().target+" "+instruction.getLoadType().source);

        cpu.getRegisters().pc++;
        if(instruction.getLoadType().source == RegisterBank.HLI || instruction.getLoadType().source == RegisterBank.D8) return CPU.CPUCYCLE_2;
        return CPU.CPUCYCLE_1;
    });

    //Vars
    InstructionRunnable runnable;
    Instruction(InstructionRunnable runnable){
        this.runnable = runnable;
    }

    private int bit;
    public int getBit() {
        return bit;
    }
    public Instruction setBit(int bit) {
        this.bit = bit;
        return this;
    }

    //Used for CALL and RST methods
    private int rstAdr;
    public int getRSTAddress(){ return rstAdr; }
    public Instruction setRSTAddress(int address){
        this.rstAdr = address;
        return this;
    }

    private RegisterBank registerBank;
    public RegisterBank getRegisterBank() {return registerBank;}
    public Instruction setTarget(RegisterBank registerBank){
        this.registerBank = registerBank;
        return this;
    }

    private JumpCondition jumpCondition;
    public JumpCondition getJumpCondition() {return jumpCondition;}
    public Instruction setJumpCondition(JumpCondition options){
        this.jumpCondition = options;
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

    private boolean clearZ;
    public boolean isClearZ(){ return clearZ;}
    public Instruction setClearZ(boolean clearZ) {
        this.clearZ = clearZ;
        return this;
    }

    private String instructionName;

    public void setInstructionName(String instructionName) {
        this.instructionName = instructionName;
    }

    public String getInstructionName() {
        return instructionName;
    }

    public interface InstructionRunnable {
        /**
         * Run the given instruction on the given CPU.
         * @param cpu
         * @param instruction
         * @return the amount of cpu cycles the given instruction requires.
         */
        int run(CPU cpu, Instruction instruction);
    }

    //Run instruction on given CPU.
    public int run(CPU cpu){
        int result = runnable.run(cpu, this);
        setClearZ(false);
        return result;
    }
}


