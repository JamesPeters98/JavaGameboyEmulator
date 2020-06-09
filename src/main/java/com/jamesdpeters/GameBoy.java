package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.gpu.Display;
import com.jamesdpeters.gpu.GPU;
import com.jamesdpeters.memory.MemoryBus;

import java.util.concurrent.TimeUnit;


public class GameBoy implements Runnable {

    public static boolean VERBOSE = false;

    public Thread thread;
    public boolean running = true;



    private CPU cpu;
    private GPU gpu;
    private Display display;

    public static GameBoy instance;
    public long startTime;

    public GameBoy(){
        cpu = new CPU();
        cpu.getRegisters().totalCycles += 4;

        boolean debugStep = false;
        display = new Display(cpu);
        gpu = new GPU(display);

        thread = new Thread(this);
    }

    public static void main(String[] args) {
        instance = new GameBoy();
        instance.thread.start();
    }

    @Override
    public void run() {
        startTime = System.nanoTime();
        while(running){

//            Utils.waitForInput();
//            System.out.println(cpu.getRegisters());

            if(!MemoryBus.isBootRomEnabled){
                VERBOSE = true;
                System.out.println(cpu.getRegisters());
                long timeTaken = System.nanoTime() - startTime;
                double clockSpeed = ((double) cpu.getRegisters().totalCycles / (Math.pow(10,-9)*timeTaken));
                System.out.println("Average Clock Speed: "+clockSpeed);
                System.out.println("Instructions: "+cpu.getRegisters().totalCycles);
                System.out.println("Average Clock Speed: "+timeTaken+" ns");
                Utils.waitForInput();
            }

            int cycle = cpu.step();
            gpu.step(cycle);

//            if(!cpu.haveTested) {
//                System.out.println();
//                System.out.println(cpu.getRegisters());
//                Utils.waitForInput();
//            }
        }
    }
}
