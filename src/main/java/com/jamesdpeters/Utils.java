package com.jamesdpeters;

public class Utils {

    public static String byteToString(byte b){
        return "0x"+Integer.toHexString(b & 0xFF);
    }

    public static String intToString(int b){
        return "0x"+Integer.toHexString(b & 0xFFFF);
    }
    public static String intToBinaryString(int b){
        return "0b"+Integer.toBinaryString(b & 0xFFFF);
    }

    public static char getHexChar(int b, int pos){
        String hexByte = Utils.intToString(b);
        return hexByte.charAt(hexByte.length()-1-pos);
    }

    public static int[] ByteToInt(byte[] bytes){
        int[] ints = new int[bytes.length];
        for (int i=0; i<bytes.length; i++){
            ints[i] = bytes[i] & 0xff;
        }
        return ints;
    }

    public static byte[] IntToByte(int[] ints){
        byte[] bytes = new byte[ints.length];
        for (int i=0; i<ints.length; i++){
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

    public static int getBit(int byte_, int position)
    {
        return (byte_ >> position) & 1;
    }
}
