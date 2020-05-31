package com.jamesdpeters.cpu;

import com.jamesdpeters.BootRom;
import com.jamesdpeters.cartridge.Cart;
import com.jamesdpeters.cpu.enums.Instruction;
import com.jamesdpeters.exceptions.UnknownInstructionException;
import com.jamesdpeters.memory.MemoryBus;

public class CPU {

    public enum State{
        RUNNING,
        HALT
    }


    //VARS
    Registers registers;
    Cart cart;
    MemoryBus memory;
    State state;

    public CPU(){
        registers = new Registers();
        cart = new Cart("tetris.gb");
        memory = cart.rom;
        registers.pc = 0x0100; //Entry point
//        memory = new MemoryBus(BootRom.GAMEBOY_CLASSIC());
        state = State.RUNNING;
        setInitialConditions();
        System.out.println(registers);
    }

    public void step(){
        if(state == State.HALT) return;
        if(state == State.RUNNING) {
            try {
                byte instructionByte = memory.getByte(registers.pc);
                Instruction instruction = InstructionBuilder.fromByteNotPrefixed(instructionByte);
                instruction.run(this);
                System.out.println(registers);
            } catch (UnknownInstructionException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public Registers getRegisters() {
        return registers;
    }

    public Cart getCart() {
        return cart;
    }

    public MemoryBus getMemory() {
        return memory;
    }

    public byte readNextByte(){
        getRegisters().pc++;
        return getMemory().getByte(getRegisters().pc);
    }

    public int readNextD16(){
        getRegisters().pc++;
        byte lsb = getMemory().getByte(getRegisters().pc);
        getRegisters().pc++;
        byte msb = getMemory().getByte(getRegisters().pc);
        return ((msb & 0xFF) << 8) | (lsb & 0xFF);
    }

    public byte readCurrentByte(){
        return getMemory().getByte(getRegisters().pc);
    }

    public void setState(State state){
        this.state = state;
    }

    public void setInitialConditions(){
        getRegisters().setAF(0x01b0);
        getRegisters().setBC(0x0013);
        getRegisters().setDE(0x00D8);
        getRegisters().setHL(0x014D);
        getRegisters().sp = (short) 0xFFFE;

        getMemory().writeByte(0xFF05, (byte) 0x00);
        getMemory().writeByte(0xFF06, (byte) 0x00);
        getMemory().writeByte(0xFF07, (byte) 0x00);
        getMemory().writeByte(0xFF10, (byte) 0x80);

    }
}
