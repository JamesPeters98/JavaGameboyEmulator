package com.jamesdpeters.exceptions;

import com.jamesdpeters.Utils;

public class DMATransferException extends Exception {

    public DMATransferException(int pc){
        super("Access to non-HRAM memory occured at PC: "+ Utils.intToString(pc));
    }
}
