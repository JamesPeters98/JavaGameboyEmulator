package com.jamesdpeters.gpu.registers;

import com.jamesdpeters.memory.MemoryBus;

public class LCDValues {

    public static int getScrollY(){
        return MemoryBus.getByte(0xFF42) & 0xFF;
    }

    public static int getScrollX(){
        return MemoryBus.getByte(0xFF43) & 0xFF;
    }

    public static int getLineY(){
        return MemoryBus.getByte(0xFF44) & 0xFF;
    }

    public static int getLineYCompare(){
        return MemoryBus.getByte(0xFF45) & 0xFF;
    }



    public static void setScrollY(int value){
        MemoryBus.writeByte(0xFF42, value & 0xFF);
    }

    public static void setScrollX(int value){
        MemoryBus.writeByte(0xFF43, value & 0xFF);
    }

    public static void setLineY(int value){
        MemoryBus.writeByte(0xFF44, value & 0xFF);
    }

    public static void setLineYCompare(int value){
        MemoryBus.writeByte(0xFF42, value & 0xFF);
    }


    public static void incrementLineY(){
        setLineY(getLineY()+1 & 0xFF);
    }



}
