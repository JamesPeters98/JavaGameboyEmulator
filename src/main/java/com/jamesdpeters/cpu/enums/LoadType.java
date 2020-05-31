package com.jamesdpeters.cpu.enums;

public class LoadType {

    RegisterBank source;
    RegisterBank target;

    LoadType(RegisterBank source, RegisterBank target){
        this.source = source;
        this.target = target;
    }
}
