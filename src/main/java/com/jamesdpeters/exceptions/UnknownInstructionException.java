package com.jamesdpeters.exceptions;

import com.jamesdpeters.monitoring.Monitor;

public class UnknownInstructionException extends Exception {

    public UnknownInstructionException(int byte_){
        super("Unknown Instruction for: 0x"+Integer.toHexString(byte_));
        Monitor.saveToCSV();
    }

}
