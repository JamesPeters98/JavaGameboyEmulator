package com.jamesdpeters;

public class Utils {

    public static String byteToString(byte b){
        return "0x"+Integer.toHexString(b & 0xFF);
    }

    public static String intToString(int b){
        return "0x"+Integer.toHexString(b & 0xFF);
    }

    public static char getHexChar(int b, int pos){
        String hexByte = Utils.intToString(b);
        return hexByte.charAt(hexByte.length()-1-pos);
    }
}
