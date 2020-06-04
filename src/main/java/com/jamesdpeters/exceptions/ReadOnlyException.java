package com.jamesdpeters.exceptions;

import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.memory.MemoryBus;

public class ReadOnlyException extends Exception {

    MemoryBus.Bank bank;
    int value, address;
    int currentInstruction;

    public ReadOnlyException(MemoryBus.Bank bank, int address, int value){
        //super("Tried to write to "+bank+" but it's Read Only!");
        this.bank = bank;
        this.value = value;
        this.address = address;
        this.currentInstruction = MemoryBus.getByte(address);
    }

    @Override
    public String getMessage() {
        return "Tried to write to "+bank+" value: "+value+" at address: "+ Utils.intToString(address)+" current instruction: "+Utils.intToString(currentInstruction);
    }
}
