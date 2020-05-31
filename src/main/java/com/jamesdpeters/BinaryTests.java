package com.jamesdpeters;

public class BinaryTests {

    public static void main(String[] args) {
        byte b1 = (byte) 0b11111111;
        byte b2 = (byte) 0b11111110;

        System.out.println("b1 : "+(Integer.toBinaryString(b1))+" b2: "+(Integer.toBinaryString(b1)));
    }

}
