package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.gpu.Display;
import com.jamesdpeters.gpu.Tile;
import com.jamesdpeters.gpu.Tiles;

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
//            if(cpu.getRegisters().totalCycles >= 229388){
//                break;
//            }
//            if(steps % 1000 == 0){
//                System.out.println("Steps: "+steps);
//                System.out.println(cpu.getRegisters());
//            }
            cpu.step();
            steps++;
        }

//        Display display = new Display();
//        int tiles = 0;
//        for(Tile tile : Tiles.getTiles()){
//            tiles++;
//            if(tiles > 360) break;
//            int row = (tile.getIndex() / 20);
//            int col = tile.getIndex() % 20;
//            display.setTile(row,col,tile);
//        }
//        while(true){
//            display.tick();
//        }

    }
}
