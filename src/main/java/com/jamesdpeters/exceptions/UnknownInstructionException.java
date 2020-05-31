package com.jamesdpeters.exceptions;

public class UnknownInstructionException extends Exception {

    public UnknownInstructionException(int byte_){
        super("Unknown Instruction for: 0x"+Integer.toHexString(byte_));
    }
}
