package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;

public class GameBoy {

    public static void main(String[] args) {
        CPU cpu = new CPU();
        while(true) {
            cpu.step();
        }

    }
}
