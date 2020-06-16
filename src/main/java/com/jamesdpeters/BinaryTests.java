package com.jamesdpeters;

public class BinaryTests {

    public static void main(String []args){
//        int b = 0b11111101;
//
//        int result = getBits(b,-1);
//
//        System.out.println(Integer.toBinaryString(result));
        int val = 0xf1;

        int adr = val * 0x100;
        int dest = adr + 0xFE00;

        System.out.println(Utils.intToString(adr));
        System.out.println(Utils.intToString(dest));
    }

    public static int setBit(int byte_, int position, boolean bool){
        if(bool) return byte_ | 1 << position;
        return byte_ & ~(1 << position);
    }

    /** Color - 0-3
     Returns 2 bits offset by pos.
     In the format 33221100.
     **/
    private static int getBits(int value, int pos){
        return (((1 << 2) - 1) & (value >> (2*pos)));
    }

}
