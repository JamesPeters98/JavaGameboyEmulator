package com.jamesdpeters.cpu;

import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.enums.Instruction;
import com.jamesdpeters.cpu.enums.JumpCondition;
import com.jamesdpeters.cpu.enums.RegisterBank;
import com.jamesdpeters.exceptions.UnknownInstructionException;
import com.jamesdpeters.exceptions.UnknownPrefixInstructionException;
import com.jamesdpeters.memory.MemoryBus;

public class InstructionBuilder {
    public static Instruction fromByteNotPrefixed(CPU cpu, int byte_) throws UnknownInstructionException, UnknownPrefixInstructionException {
        int b = byte_ & 0xFF;

        if(b == 0xCB) return fromBytePrefixed(MemoryBus.getByte(cpu.registers.pc+1));

        if(b == 0x00) return Instruction.NOP;
        if(b == 0x76) return Instruction.HALT;
        if(b == 0xf3) return Instruction.DI;
        if(b == 0xfb) return Instruction.EI;
        if(b == 0x2f) return Instruction.CPL;
        if(b == 0xe9) return Instruction.JP_HL;

        //OPERATIONS AND BITWISE
        if(inHorizontalRange(b,  0x80,  0x87)) return horizontalLoadType(Instruction.ADD,  0x80, b, RegisterBank.A);
        if(inHorizontalRange(b,  0x90,  0x97)) return horizontalLoadType(Instruction.SUB,  0x90, b, RegisterBank.A);
        if(inHorizontalRange(b,  0xA0,  0xA7)) return horizontalLoadType(Instruction.AND,  0xA0, b, RegisterBank.A);
        if(inHorizontalRange(b,  0xB0,  0xB7)) return horizontalLoadType(Instruction.OR,  0xB0, b, RegisterBank.A);
        if(inHorizontalRange(b,  0xA8, 0xAF)) return horizontalLoadType(Instruction.XOR, 0xA8, b, RegisterBank.A);
        if(inHorizontalRange(b,  0xB8, 0xBF)) return horizontalLoadType(Instruction.CP, 0xB8, b, RegisterBank.A);

        //D8 OPERATIONS
        if(b == 0xC6) return Instruction.ADD.setLoadType(RegisterBank.D8,RegisterBank.A);
        if(b == 0xD6) return Instruction.SUB.setLoadType(RegisterBank.D8,RegisterBank.A);
        if(b == 0xE6) return Instruction.AND.setLoadType(RegisterBank.D8,RegisterBank.A);
        if(b == 0xF6) return Instruction.OR.setLoadType(RegisterBank.D8,RegisterBank.A);

        //HL ADD
        if(inVerticalRange(b,0x09,0x39)) return verticalTargetBCDEHLSP(Instruction.ADD_HL,0x09,b);

        //INCREMENTS
        if(inVerticalRange(b,  0x0C,  0x3C)) return verticalTargetCELA(Instruction.INC,  0x0C, b);
        if(inVerticalRange(b,  0x04,  0x34)) return verticalTargetBDHHL(Instruction.INC,  0x04, b);
        if(inVerticalRange(b,  0x03,  0x33)) return verticalTargetBCDEHLSP(Instruction.INC_16,  0x03, b);

        //DECREMENTS
        if(inVerticalRange(b,  0x0D,  0x3D)) return verticalTargetCELA(Instruction.DEC,  0x0D, b);
        if(inVerticalRange(b,  0x05,  0x35)) return verticalTargetBDHHL(Instruction.DEC,  0x05, b);
        if(inVerticalRange(b,  0x0B,  0x3B)) return verticalTargetBCDEHLSP(Instruction.DEC_16,  0x0B, b);


        //JUMP INSTRUCTIONS
        if(b == 0xC3) return Instruction.JP_N16.setJumpCondition(JumpCondition.ALWAYS);
        if(b == 0xC2) return Instruction.JP_N16.setJumpCondition(JumpCondition.NZ);
        if(b == 0xD2) return Instruction.JP_N16.setJumpCondition(JumpCondition.NC);
        if(b == 0xCA) return Instruction.JP_N16.setJumpCondition(JumpCondition.Z);
        if(b == 0xDA) return Instruction.JP_N16.setJumpCondition(JumpCondition.C);

        //RETURN INSTRUCTIONS
        if(b == 0xC0) return Instruction.RET.setJumpCondition(JumpCondition.NZ);
        if(b == 0xD0) return Instruction.RET.setJumpCondition(JumpCondition.NC);
        if(b == 0xC8) return Instruction.RET.setJumpCondition(JumpCondition.Z);
        if(b == 0xD8) return Instruction.RET.setJumpCondition(JumpCondition.C);
        if(b == 0xC9) return Instruction.RET.setJumpCondition(JumpCondition.ALWAYS);
        if(b == 0xD9) return Instruction.RET.setJumpCondition(JumpCondition.ALWAYS_IME);

        //JR
        if(b ==  0x18) return Instruction.JR.setJumpCondition(JumpCondition.ALWAYS);
        if(b ==  0x20) return Instruction.JR.setJumpCondition(JumpCondition.NZ);
        if(b ==  0x30) return Instruction.JR.setJumpCondition(JumpCondition.NC);
        if(b ==  0x28) return Instruction.JR.setJumpCondition(JumpCondition.Z);
        if(b ==  0x38) return Instruction.JR.setJumpCondition(JumpCondition.C);

        //CALL
        if(b == 0xCD) return Instruction.CALL.setJumpCondition(JumpCondition.ALWAYS);
        if(b == 0xCC) return Instruction.CALL.setJumpCondition(JumpCondition.Z);
        if(b == 0xDC) return Instruction.CALL.setJumpCondition(JumpCondition.C);
        if(b == 0xC4) return Instruction.CALL.setJumpCondition(JumpCondition.NZ);
        if(b == 0xD4) return Instruction.CALL.setJumpCondition(JumpCondition.NC);

        //RST CALLS
        if(inVerticalRange(b,0xC7, 0xF7)) return verticalRST(0xC7, b, 0x00);
        if(inVerticalRange(b,0xCF, 0xFF)) return verticalRST(0xCF, b, 0x08);

        //PUSH
//        if(b == 0xC5) return Instruction.PUSH.setTarget(RegisterBank.BC);
//        if(b == 0xD5) return Instruction.PUSH.setTarget(RegisterBank.DE);
//        if(b == 0xE5) return Instruction.PUSH.setTarget(RegisterBank.HL);
//        if(b == 0xF5) return Instruction.PUSH.setTarget(RegisterBank.AF);

        //LOAD INSTRUCTIONS
        if(inHorizontalRange(b,  0x40, 0x47)) return horizontalLoadType(Instruction.LD,  0x40, b, RegisterBank.B);
        if(inHorizontalRange(b,  0x50, 0x57)) return horizontalLoadType(Instruction.LD,  0x50, b, RegisterBank.D);
        if(inHorizontalRange(b,  0x60, 0x67)) return horizontalLoadType(Instruction.LD,  0x60, b, RegisterBank.H);
        if(inHorizontalRange(b,  0x70, 0x75)) return horizontalLoadType(Instruction.LD,  0x70, b, RegisterBank.HLI);
        if(b == 0x77) return horizontalLoadType(Instruction.LD,  0x77, b, RegisterBank.HLI);
        if(inHorizontalRange(b,  0x48, 0x4F)) return horizontalLoadType(Instruction.LD,  0x48, b, RegisterBank.C);
        if(inHorizontalRange(b,  0x58, 0x5F)) return horizontalLoadType(Instruction.LD,  0x58, b, RegisterBank.E);
        if(inHorizontalRange(b,  0x68, 0x6F)) return horizontalLoadType(Instruction.LD,  0x68, b, RegisterBank.L);
        if(inHorizontalRange(b,  0x78, 0x7F)) return horizontalLoadType(Instruction.LD,  0x78, b, RegisterBank.A);

        //A16 Load
        if(b == 0xEA) return Instruction.LD_16.setLoadType(RegisterBank.A,RegisterBank.A16);
        if(b == 0xFA) return Instruction.LD_16.setLoadType(RegisterBank.A16,RegisterBank.A);

        //16 Bit Load Operations.
        if(b == 0x02) return Instruction.LD_R16_A.setLoadType(RegisterBank.A, RegisterBank.BC_POINTER);
        if(b == 0x12) return Instruction.LD_R16_A.setLoadType(RegisterBank.A, RegisterBank.DE_POINTER);
        if(b == 0x22) return Instruction.LD_R16_A.setLoadType(RegisterBank.A, RegisterBank.HLI_PLUS);
        if(b == 0x32) return Instruction.LD_R16_A.setLoadType(RegisterBank.A, RegisterBank.HLI_MINUS);

        if(b == 0x0A) return Instruction.LD_R16_A.setLoadType(RegisterBank.BC_POINTER, RegisterBank.A);
        if(b == 0x1A) return Instruction.LD_R16_A.setLoadType(RegisterBank.DE_POINTER, RegisterBank.A);
        if(b == 0x2A) return Instruction.LD_R16_A.setLoadType(RegisterBank.HLI_PLUS, RegisterBank.A);
        if(b == 0x3A) return Instruction.LD_R16_A.setLoadType(RegisterBank.HLI_MINUS, RegisterBank.A);

        if(inVerticalRange(b, 0x01,  0x31)) return verticalLoadType( 0x01, b, RegisterBank.D16);
        if(inVerticalRange(b, 0x0e,  0x3e)) return verticalTargetCELA(Instruction.LD,  0x0e, b, RegisterBank.D8);
        if(inVerticalRange(b, 0x06,  0x36)) return verticalTargetBDHHL(Instruction.LD,  0x06, b, RegisterBank.D8);

        //LDH
        if(b == 0xe0) return Instruction.LDH.setLoadType(RegisterBank.A, RegisterBank.A8);
        if(b == 0xf0) return Instruction.LDH.setLoadType(RegisterBank.A8, RegisterBank.A);
        if(b == 0xe2) return Instruction.LD.setLoadType(RegisterBank.A, RegisterBank.C_POINTER);
        if(b == 0xf2) return Instruction.LD.setLoadType(RegisterBank.C_POINTER, RegisterBank.A);

        //CP
        if(b == 0xfe) return Instruction.CP.setLoadType(RegisterBank.D8,RegisterBank.A);


        //Rotate Operations
        if(b == 0xf) return Instruction.RRC.setLoadType(RegisterBank.A, null).setClearZ(true);
        if(b == 0x1f) return Instruction.RR.setLoadType(RegisterBank.A, null).setClearZ(true);
        if(b == 0x7) return Instruction.RLC.setLoadType(RegisterBank.A, null).setClearZ(true);
        if(b == 0x17) return Instruction.RL.setLoadType(RegisterBank.A, null).setClearZ(true);

        //POP / PUSH
        if(inVerticalRange(b, 0xC1,0xF1)) return verticalTargetBCDEHLAF(Instruction.POP, 0xC1, b, RegisterBank.SP_DATA);
        if(inVerticalRange(b, 0xC5,0xF5)) return verticalTargetBCDEHLAF(Instruction.PUSH, 0xC5, b, RegisterBank.SP_DATA);

        //No instruction
        System.out.println();
        System.err.println("Current clock cycle: "+cpu.getRegisters().totalCycles);
        System.err.println("PC: "+Utils.intToString(cpu.getRegisters().pc));
        throw new UnknownInstructionException(b);
    }

    private static Instruction fromBytePrefixed(int b) throws UnknownPrefixInstructionException, UnknownInstructionException {
        //SET
        if(inHorizontalRange(b,  0xC0,  0xC7)) return horizontalLoadType(Instruction.SET,  0xC0, b, null).setBit(0);
        if(inHorizontalRange(b,  0xC8,  0xCF)) return horizontalLoadType(Instruction.SET,  0xC8, b, null).setBit(1);
        if(inHorizontalRange(b,  0xD0,  0xD7)) return horizontalLoadType(Instruction.SET,  0xD0, b, null).setBit(2);
        if(inHorizontalRange(b,  0xD8,  0xDF)) return horizontalLoadType(Instruction.SET,  0xD8, b, null).setBit(3);
        if(inHorizontalRange(b,  0xE0,  0xE7)) return horizontalLoadType(Instruction.SET,  0xE0, b, null).setBit(4);
        if(inHorizontalRange(b,  0xE8,  0xEF)) return horizontalLoadType(Instruction.SET,  0xE8, b, null).setBit(5);
        if(inHorizontalRange(b,  0xF0,  0xF7)) return horizontalLoadType(Instruction.SET,  0xF0, b, null).setBit(6);
        if(inHorizontalRange(b,  0xF8,  0xFF)) return horizontalLoadType(Instruction.SET,  0xF8, b, null).setBit(7);

        //BIT
        if(inHorizontalRange(b,  0x40,  0x47)) return horizontalLoadType(Instruction.BIT,  0x40, b, null).setBit(0);
        if(inHorizontalRange(b,  0x48,  0x4F)) return horizontalLoadType(Instruction.BIT,  0x48, b, null).setBit(1);
        if(inHorizontalRange(b,  0x50,  0x57)) return horizontalLoadType(Instruction.BIT,  0x50, b, null).setBit(2);
        if(inHorizontalRange(b,  0x58,  0x5F)) return horizontalLoadType(Instruction.BIT,  0x58, b, null).setBit(3);
        if(inHorizontalRange(b,  0x60,  0x67)) return horizontalLoadType(Instruction.BIT,  0x60, b, null).setBit(4);
        if(inHorizontalRange(b,  0x68,  0x6F)) return horizontalLoadType(Instruction.BIT,  0x68, b, null).setBit(5);
        if(inHorizontalRange(b,  0x70,  0x77)) return horizontalLoadType(Instruction.BIT,  0x70, b, null).setBit(6);
        if(inHorizontalRange(b,  0x78,  0x7F)) return horizontalLoadType(Instruction.BIT,  0x78, b, null).setBit(7);

        //RES
        if(inHorizontalRange(b,  0x80,  0x87)) return horizontalLoadType(Instruction.RES,  0x80, b, null).setBit(0);
        if(inHorizontalRange(b,  0x88,  0x8F)) return horizontalLoadType(Instruction.RES,  0x88, b, null).setBit(1);
        if(inHorizontalRange(b,  0x90,  0x97)) return horizontalLoadType(Instruction.RES,  0x90, b, null).setBit(2);
        if(inHorizontalRange(b,  0x98,  0x9F)) return horizontalLoadType(Instruction.RES,  0x98, b, null).setBit(3);
        if(inHorizontalRange(b,  0xA0,  0xA7)) return horizontalLoadType(Instruction.RES,  0xA0, b, null).setBit(4);
        if(inHorizontalRange(b,  0xA8,  0xAF)) return horizontalLoadType(Instruction.RES,  0xA8, b, null).setBit(5);
        if(inHorizontalRange(b,  0xB0,  0xB7)) return horizontalLoadType(Instruction.RES,  0xB0, b, null).setBit(6);
        if(inHorizontalRange(b,  0xB8,  0xBF)) return horizontalLoadType(Instruction.RES,  0xB8, b, null).setBit(7);

        //RLC
        if(inHorizontalRange(b,  0x00,  0x07)) return horizontalLoadType(Instruction.RLC,  0x0, b, null);
        if(inHorizontalRange(b,  0x10,  0x17)) return horizontalLoadType(Instruction.RL,  0x10, b, null);
        if(inHorizontalRange(b,  0x08,  0x0F)) return horizontalLoadType(Instruction.RRC,  0x08, b, null);
        if(inHorizontalRange(b,  0x18,  0x1F)) return horizontalLoadType(Instruction.RRC,  0x18, b, null);

        //SWAP
        if(inHorizontalRange(b,  0x30,  0x37)) return horizontalLoadType(Instruction.SWAP,  0x30, b, null);

        throw new UnknownPrefixInstructionException(b);
    }
    private static boolean inHorizontalRange(int byte_, int start, int end){
        return (byte_ >= start) && (byte_ <= end);
    }

    private static boolean inHorizontalRange(int byte_, byte start, byte end){
        return (byte_ >= start) && (byte_ <= end);
    }

    private static boolean inVerticalRange(int byte_, int start, int end){
        return (byte_ >= start) && (byte_ <= end) && Utils.getHexChar(byte_,0)==Utils.getHexChar(start,0);
    }

    private static Instruction horizontalLoadType(Instruction instruction, int start, int value, RegisterBank target) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setLoadType(RegisterBank.B,target);
            case 1: return instruction.setLoadType(RegisterBank.C,target);
            case 2: return instruction.setLoadType(RegisterBank.D,target);
            case 3: return instruction.setLoadType(RegisterBank.E,target);
            case 4: return instruction.setLoadType(RegisterBank.H,target);
            case 5: return instruction.setLoadType(RegisterBank.L,target);
            case 6: return instruction.setLoadType(RegisterBank.HLI,target);
            case 7: return instruction.setLoadType(RegisterBank.A,target);
        }
        throw new UnknownInstructionException(value);
    }

    private static Instruction horizontalLoadType(Instruction instruction, byte start, int value, RegisterBank target) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setLoadType(RegisterBank.B,target);
            case 1: return instruction.setLoadType(RegisterBank.C,target);
            case 2: return instruction.setLoadType(RegisterBank.D,target);
            case 3: return instruction.setLoadType(RegisterBank.E,target);
            case 4: return instruction.setLoadType(RegisterBank.H,target);
            case 5: return instruction.setLoadType(RegisterBank.L,target);
            case 6: return instruction.setLoadType(RegisterBank.HLI,target);
            case 7: return instruction.setLoadType(RegisterBank.A,target);
        }
        throw new UnknownInstructionException(value);
    }

    private static Instruction verticalLoadType(int start, int value, RegisterBank source) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0: return Instruction.LD_16.setLoadType(source,RegisterBank.BC);
            case 0x10: return Instruction.LD_16.setLoadType(source,RegisterBank.DE);
            case 0x20: return Instruction.LD_16.setLoadType(source,RegisterBank.HL);
            case 0x30: return Instruction.LD_16.setLoadType(source,RegisterBank.SP);
        }
        throw new UnknownInstructionException(value);
    }

    private static Instruction horizontalTarget(Instruction instruction, byte start, int value) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setTarget(RegisterBank.B);
            case 1: return instruction.setTarget(RegisterBank.C);
            case 2: return instruction.setTarget(RegisterBank.D);
            case 3: return instruction.setTarget(RegisterBank.E);
            case 4: return instruction.setTarget(RegisterBank.H);
            case 5: return instruction.setTarget(RegisterBank.L);
            case 6: return instruction.setTarget(RegisterBank.HLI);
            case 7: return instruction.setTarget(RegisterBank.A);
        }
        throw new UnknownInstructionException(value);
    }

    private static Instruction verticalTargetCELA(Instruction instruction, int start, int value, RegisterBank source) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setLoadType(source,RegisterBank.C);
            case 0x10: return instruction.setLoadType(source,RegisterBank.E);
            case 0x20: return instruction.setLoadType(source,RegisterBank.L);
            case 0x30: return instruction.setLoadType(source,RegisterBank.A);
        }
        throw new UnknownInstructionException(value);
    }

    private static Instruction verticalTargetBDHHL(Instruction instruction, int start, int value, RegisterBank source) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setLoadType(source,RegisterBank.B);
            case 0x10: return instruction.setLoadType(source,RegisterBank.D);
            case 0x20: return instruction.setLoadType(source,RegisterBank.H);
            case 0x30: return instruction.setLoadType(source,RegisterBank.HLI);
        }
        throw new UnknownInstructionException(value);
    }

    private static Instruction verticalTargetCELA(Instruction instruction, int start, int value) {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setTarget(RegisterBank.C);
            case 0x10: return instruction.setTarget(RegisterBank.E);
            case 0x20: return instruction.setTarget(RegisterBank.L);
            case 0x30: return instruction.setTarget(RegisterBank.A);
        }
        return null;
    }

    private static Instruction verticalTargetBDHHL(Instruction instruction, int start, int value) {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setTarget(RegisterBank.B);
            case 0x10: return instruction.setTarget(RegisterBank.D);
            case 0x20: return instruction.setTarget(RegisterBank.H);
            case 0x30: return instruction.setTarget(RegisterBank.HLI);
        }
        return null;
    }

    private static Instruction verticalTargetBCDEHLSP(Instruction instruction, int start, int value) {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setTarget(RegisterBank.BC);
            case 0x10: return instruction.setTarget(RegisterBank.DE);
            case 0x20: return instruction.setTarget(RegisterBank.HL);
            case 0x30: return instruction.setTarget(RegisterBank.SP);
        }
        return null;
    }

    private static Instruction verticalTargetBCDEHLAF(Instruction instruction, int start, int value, RegisterBank source) {
        int offset = value - start;
        switch (offset){
            case 0: return instruction.setLoadType(source,RegisterBank.BC);
            case 0x10: return instruction.setLoadType(source,RegisterBank.DE);
            case 0x20: return instruction.setLoadType(source,RegisterBank.HL);
            case 0x30: return instruction.setLoadType(source,RegisterBank.AF);
        }
        return null;
    }

    private static Instruction verticalRST(int start, int value, int startAdr) throws UnknownInstructionException {
        int offset = value - start;
        switch (offset){
            case 0:
            case 0x10:
            case 0x20:
            case 0x30:
                return Instruction.RST.setRSTAddress(startAdr+offset);
        }
        throw new UnknownInstructionException(value);
    }
}
