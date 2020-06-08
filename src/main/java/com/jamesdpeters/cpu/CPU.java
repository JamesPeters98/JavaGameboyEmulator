package com.jamesdpeters.cpu;

import com.jamesdpeters.BootRom;
import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cartridge.Cart;
import com.jamesdpeters.cpu.enums.Instruction;
import com.jamesdpeters.exceptions.UnknownInstructionException;
import com.jamesdpeters.exceptions.UnknownPrefixInstructionException;
import com.jamesdpeters.memory.MemoryBus;

public class CPU {

    public enum State{
        RUNNING,
        HALT
    }

    public final static int CPUCYCLE_1 = 4;
    public final static int CPUCYCLE_2 = 8;
    public final static int CPUCYCLE_3 = 12;
    public final static int CPUCYCLE_4 = 16;
    public final static int CPUCYCLE_5 = 20;
    public final static int CPUCYCLE_6 = 24;

    //VARS
    Registers registers;
    Cart cart;
    State state;

    public CPU(){
        registers = new Registers();
        cart = new Cart("tetris.gb");
        MemoryBus.setROM(cart.rom);
        MemoryBus.setBootROM(BootRom.GAMEBOY_CLASSIC);
        state = State.RUNNING;
        //setInitialConditions();
        System.out.print(registers);
    }

    public int step(){
        if(state == State.HALT) return 0;
        if(state == State.RUNNING) {
            try {
                int instructionByte = MemoryBus.getByte(registers.pc);
                Instruction instruction = InstructionBuilder.fromByteNotPrefixed(this,instructionByte);
                int cycle = instruction.run(this);
                getRegisters().totalCycles += cycle;
                if(GameBoy.VERBOSE) {
                    if(instructionByte == 0xCB){
                        int instructionByteNext = MemoryBus.getByte(registers.pc);
                        System.out.println(" | Next Instruction: 0xCB -> "+Utils.intToString(instructionByteNext) + " (" + instruction.getInstructionName() + ") - CPU Cycles: "+cycle);
                    } else {
                        System.out.println(" | Next Instruction: " + Utils.intToString(instructionByte) + " (" + instruction.getInstructionName() + ") - CPU Cycles: " + cycle);
                    }
                    System.out.print(registers);
                }
                return cycle;
            } catch (UnknownInstructionException | UnknownPrefixInstructionException e) {
                System.out.println();
                e.printStackTrace();
                System.exit(-1);
            }
        }
        return 0;
    }

    public Registers getRegisters() {
        return registers;
    }

    public Cart getCart() {
        return cart;
    }

    public int readNextByte(){
        getRegisters().pc++;
        return MemoryBus.getByte(getRegisters().pc);
    }

    public int readNextD16(){
        getRegisters().pc++;
        int lsb = MemoryBus.getByte(getRegisters().pc) & 0xFF;
        getRegisters().pc++;
        int msb = MemoryBus.getByte(getRegisters().pc);
        return msb << 8 | lsb;
    }

    public int readCurrentByte(){
        return MemoryBus.getByte(getRegisters().pc);
    }

    public void setState(State state){
        this.state = state;
    }

    public void setInitialConditions(){
        getRegisters().setAF(0x01b0);
        getRegisters().setBC(0x000D);
        getRegisters().setDE(0x00D8);
        getRegisters().setHL(0x014D);
        getRegisters().sp = (short) 0xFFFE;

//        getRegisters().getF().ZERO = true;
//        getRegisters().getF().SUBTRACT = false;
//        getRegisters().getF().HALF_CARRY = true;
//        getRegisters().getF().CARRY = true;

//        getMemory().writeByte(0xFF05, (byte) 0x00);
//        getMemory().writeByte(0xFF06, (byte) 0x00);
//        getMemory().writeByte(0xFF07, (byte) 0x00);
//        getMemory().writeByte(0xFF10, (byte) 0x80);

    }
}
