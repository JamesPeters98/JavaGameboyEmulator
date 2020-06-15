package com.jamesdpeters.gpu;

import java.util.HashMap;

public class Sprite {

    private static Sprite[] sprites = new Sprite[40];

    /**
     * Set a specific byte in OAM.
     * @param address offset from 0xFE00.
     * @param b byte.
     */
    public static void setOAM(int address, int b){

    }

    private int OAMIndex;
    private int Y,X, tileNumber, attributes;
    public Sprite(){

    }
}
