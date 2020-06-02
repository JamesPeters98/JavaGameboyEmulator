package com.jamesdpeters.gpu;

public class Tiles {

    public enum PixelValue {
        WHITE, //11
        DARK_GRAY, //10
        LIGHT_GRAY, //01
        BLACK; //00
    }

    public static PixelValue getPixelValue(int lsb, int msb){
        if(lsb == 1 & msb == 1) return PixelValue.WHITE;
        if(lsb == 0 & msb == 1) return PixelValue.DARK_GRAY;
        if(lsb == 1 & msb == 0) return PixelValue.LIGHT_GRAY;
        if(lsb == 0 & msb == 0) return PixelValue.BLACK;

        return PixelValue.WHITE;
    }

    private static PixelValue[][][] tiles = new PixelValue[384][8][8];

    public static void setTilePixel(int tileIndex, int rowIndex, int pixelIndex, PixelValue pixelValue){
        tiles[tileIndex][rowIndex][pixelIndex] = pixelValue;
        System.out.println("Setting Tile "+tileIndex+" row:"+" pixel: "+pixelIndex+" to value: "+pixelValue);
        System.exit(0);
    }



}
