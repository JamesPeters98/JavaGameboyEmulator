package com.jamesdpeters.gpu;

import java.awt.*;
import java.util.Collection;
import java.util.TreeMap;

public class Tiles {

    public enum PixelValue {
        WHITE, //11
        DARK_GRAY, //10
        LIGHT_GRAY, //01
        BLACK; //00

        public int getRGB(){
            return Tiles.getRGB(this);
        }
    }

    private final static Color WHITE = new Color(255,255,255);
    private final static Color LIGHT_GRAY = new Color(178,178,178);
    private final static Color DARK_GRAY = new Color(102,102,102);
    private final static Color BLACK = new Color(0,0,0);

    private static int getRGB(PixelValue pixelValue){
        switch (pixelValue){
            case WHITE: return WHITE.getRGB();
            case LIGHT_GRAY: return LIGHT_GRAY.getRGB();
            case DARK_GRAY: return DARK_GRAY.getRGB();
            case BLACK: return BLACK.getRGB();

            default: return WHITE.getRGB();
        }
    }

    public static PixelValue getPixelValue(int lsb, int msb){
        if(lsb == 1 & msb == 1) return PixelValue.WHITE;
        if(lsb == 0 & msb == 1) return PixelValue.DARK_GRAY;
        if(lsb == 1 & msb == 0) return PixelValue.LIGHT_GRAY;
        if(lsb == 0 & msb == 0) return PixelValue.BLACK;

        return PixelValue.WHITE;
    }

    private static TreeMap<Integer, Tile> tileMap = new TreeMap<>();

    public static void setTilePixel(int tileIndex, int rowIndex, int pixelIndex, PixelValue pixelValue){
        Tile tile = tileMap.get(tileIndex);
        if(tile == null) {
            tile = new Tile(tileIndex);
            tileMap.put(tileIndex, tile);
        }
        tile.setPixel(rowIndex, pixelIndex, pixelValue);
        //System.out.println("Setting Tile "+tileIndex+" row:"+" pixel: "+pixelIndex+" to value: "+pixelValue);
    }

    public static Collection<Tile> getTiles(){
        return tileMap.values();
    }



}
