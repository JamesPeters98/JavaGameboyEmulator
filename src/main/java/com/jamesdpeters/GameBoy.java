package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.gpu.Display;
import com.jamesdpeters.gpu.GPU;
import com.jamesdpeters.gpu.Tile;
import com.jamesdpeters.gpu.Tiles;
import com.jamesdpeters.memory.MemoryBus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Scanner;

public class GameBoy {

    public final static boolean VERBOSE = false;

    public static void main(String[] args) {
//        Display display = new Display();
//
//        while(true){
//            display.tick();
//        }



        CPU cpu = new CPU();
        cpu.getRegisters().totalCycles += 4;
        int steps =0;

        boolean debugStep = false;
        Display display = new Display(cpu);
        GPU gpu = new GPU(display);

        while(true) {
//            if(cpu.getRegisters().pc == 0x0042 || debugStep){
//                debugStep = true;
//
//                int tiles = 0;
//                for(Tile tile : Tiles.getTiles()){
//                    tiles++;
//                    if(tiles > 360) break;
//                    int row = (tile.getIndex() / 20);
//                    int col = tile.getIndex() % 20;
//                    display.setTile(row,col,tile);
//                }
//                display.draw();
//                //Utils.waitForInput();
//            }
//            if(cpu.getRegisters().totalCycles >= 230412){
//                break;
//            }
//            if(steps % 1000 == 0){
//                System.out.println("Steps: "+steps);
//                System.out.println(cpu.getRegisters());
//            }
            int cycle = cpu.step();
            gpu.step(cycle);

            steps++;
        }

        //System.out.println(MemoryBus.Bank.HIGH_RAM.toByteString());


//        while(true){
//            display.tick();
//        }

    }
}
