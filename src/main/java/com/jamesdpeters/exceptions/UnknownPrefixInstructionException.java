package com.jamesdpeters.exceptions;

public class UnknownPrefixInstructionException extends Exception {

    public UnknownPrefixInstructionException(int byte_){
        super("Unknown Prefix Instruction for: 0xCB -> 0x"+Integer.toHexString(byte_));
    }

}
