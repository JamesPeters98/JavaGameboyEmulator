package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GameBoy {

    public final static boolean VERBOSE = true;

    public static void main(String[] args) {
//        Display display = new Display();
//
//        while(true){
//            display.tick();
//        }



        CPU cpu = new CPU();
        cpu.getRegisters().totalCycles += 4;
        int steps =0;
        while(true) {
            if(cpu.getRegisters().totalCycles >= 100000){
                System.exit(0);
            }
//            if(steps % 1000 == 0){
//                System.out.println("Steps: "+steps);
//                System.out.println(cpu.getRegisters());
//            }
            cpu.step();
            steps++;
        }


    }
}
