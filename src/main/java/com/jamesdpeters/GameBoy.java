package com.jamesdpeters;

import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.gpu.Display;
import com.jamesdpeters.gpu.GPU;
import com.jamesdpeters.memory.MemoryBus;


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
        display.start();
//        Display displayTileMap = new Display(cpu);
//        displayTileMap.setDimensions(160,300);
//        displayTileMap.setTitle("TileMap");
        GPU gpu = new GPU(display);

        while(true) {
//            if(cpu.getRegisters().pc == 0x0072){
//                System.out.println(cpu.getRegisters());
//                System.out.println("Line Y: "+ LCDValues.getLineY()+" Scroll Count: "+LCDValues.getScrollY());
////                debugStep = true;
//                Utils.waitForInput();
//            }
//            if(LCDValues.getLineY() >= 153 || debugStep) {
//                System.out.println(cpu.getRegisters());
//                System.out.println("Line Y: "+ LCDValues.getLineY()+" Scroll Count: "+LCDValues.getScrollY());
////                debugStep = true;
//                Utils.waitForInput();
//            }

//            if(cpu.getRegisters().pc == 0x0042 || debugStep){
//                debugStep = true;
//
//                for(Tile tile : Tiles.getTiles()){
//                    int row = (tile.getIndex() / 20);
//                    int col = tile.getIndex() % 20;
//                    try {
//                        displayTileMap.setTile(row, col, tile);
//                    } catch (ArrayIndexOutOfBoundsException e){
//                        e.printStackTrace();
//                        System.err.println("Row: "+row+" Col: "+col);
//                    }
//                }
//                displayTileMap.draw();
//                //Utils.waitForInput();
//            }
//            if(cpu.getRegisters().totalCycles >= 230412){
//                break;
//            }
//            if(steps % 1000 == 0){
//                System.out.println("Steps: "+steps);
//                System.out.println(cpu.getRegisters());
//            }

//            if(!MemoryBus.isBootRomEnabled){
//                System.out.println("Bootrom finished!");
////                System.out.println(MemoryBus.Bank.VRAM.toByteString());
////                try (FileOutputStream stream = new FileOutputStream("outputs/VRAM")) {
////                    stream.write(MemoryBus.Bank.VRAM.toBytes());
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//                Utils.waitForInput();
//            }
            if(!MemoryBus.isBootRomEnabled){
                System.out.println(cpu.getRegisters());
                Utils.waitForInput();
            }
            int cycle = cpu.step();
            gpu.step(cycle);
//            display.tick();

            steps++;
        }



//        while(true){
//            display.tick();
//        }

    }
}
