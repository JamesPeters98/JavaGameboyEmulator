package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.gpu.Display;
import com.jamesdpeters.gpu.GPU;
import com.jamesdpeters.gpu.Tile;
import com.jamesdpeters.gpu.Tiles;
import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDValues;
import com.jamesdpeters.memory.MemoryBus;
import com.jamesdpeters.monitoring.Monitor;
import com.jamesdpeters.monitoring.SpriteWindow;
import javafx.application.Application;

import java.util.concurrent.TimeUnit;


public class GameBoy implements Runnable {

    public static boolean VERBOSE = false;

    public Thread thread;
    public boolean running = true;

    private CPU cpu;
    private GPU gpu;
    private Display display;

    public Display backgroundMap;
    public SpriteWindow spriteViewer;
//    public Display spriteMap;

    public static GameBoy instance;
    private long startTime;
    public boolean debugStep = true;

    public GameBoy(){
        cpu = new CPU();
        cpu.getRegisters().totalCycles += 4;
        Joypad.init();

        backgroundMap = Display.BACKGROUND_TILE_DISPLAY();
        backgroundMap.setTitle("Background Map");

        display = new Display(cpu);

        spriteViewer = new SpriteWindow();
        gpu = new GPU(display,true);
        thread = new Thread(this);
    }

    public static void main(String[] args) {
        instance = new GameBoy();
        instance.thread.start();
    }

    @Override
    public void run() {
        spriteViewer.open();
        startTime = System.nanoTime();
        while(running){


//            if(debugStep && !MemoryBus.isBootRomEnabled) {
//                VERBOSE = true;
//                debugStep = true;
//                Utils.waitForInput();
//                System.out.println(cpu.getRegisters());
////                System.out.println(LCDControl.getString());
//            }

//            if(!MemoryBus.isBootRomEnabled){
//                VERBOSE = true;
//                System.out.println(cpu.getRegisters());
//                long timeTaken = System.nanoTime() - startTime;
//                double clockSpeed = ((double) cpu.getRegisters().totalCycles / (Math.pow(10,-9)*timeTaken));
//                System.out.println("Average Clock Speed: "+clockSpeed);
//                System.out.println("Instructions: "+cpu.getRegisters().totalCycles);
//                System.out.println("Average Clock Speed: "+timeTaken+" ns");
//                Utils.waitForInput();
//            }

            int cycle = cpu.step();
            gpu.step(cycle);

//            if(!MemoryBus.isBootRomEnabled) {
//                VERBOSE = true;
//                Utils.waitForInput();
//                System.out.println();
//                System.out.println(cpu.getRegisters());
//            }
        }
    }

    public static CPU getCpu() {
        return instance.cpu;
    }

    public Display getDisplay() {
        return display;
    }

    public GPU getGpu() {
        return gpu;
    }

}
