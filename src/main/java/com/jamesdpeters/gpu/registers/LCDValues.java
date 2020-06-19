package com.jamesdpeters.gpu.registers;

import com.jamesdpeters.memory.MemoryBus;

public class LCDValues {

    public static int getScrollY(){
        return MemoryBus.getByteDuringDMA(0xFF42) & 0xFF;
    }

    public static int getScrollX(){
        return MemoryBus.getByteDuringDMA(0xFF43) & 0xFF;
    }

    public static int getLineY(){
        return MemoryBus.getByteDuringDMA(0xFF44) & 0xFF;
    }

    public static int getLineYCompare(){
        return MemoryBus.getByteDuringDMA(0xFF45) & 0xFF;
    }


    public static void setScrollY(int value){
        MemoryBus.writeByteDuringDMA(0xFF42, value & 0xFF);
    }

    public static void setScrollX(int value){
        MemoryBus.writeByteDuringDMA(0xFF43, value & 0xFF);
    }

    public static void setLineY(int value){
        MemoryBus.writeByteDuringDMA(0xFF44, value & 0xFF);
    }

    public static void setLineYCompare(int value){
        MemoryBus.writeByteDuringDMA(0xFF45, value & 0xFF);
    }


    public static void incrementLineY(){
        setLineY(getLineY()+1 & 0xFF);
    }

    public static String getString() {
        return String.format("LCDValues: LineY:d%d, ScrollY: %d, ScrollX: %d", getLineY(),getScrollY(),getScrollX());
    }
}
