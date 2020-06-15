package com.jamesdpeters.gpu;

import java.awt.*;
import java.util.Collection;
import java.util.TreeMap;

public class Tiles {

    private static TreeMap<Integer, Tile> tileMap = new TreeMap<>();

    public static void setTilePixel(int tileIndex, int rowIndex, int pixelIndex, PixelValue pixelValue){
        Tile tile = tileMap.get(tileIndex);
        if(tile == null) {
            tile = new Tile(tileIndex);
            tileMap.put(tileIndex, tile);
        }
        tile.setPixel(rowIndex, pixelIndex, pixelValue);
    }

    public static PixelValue getTilePixel(int tileIndex, int rowIndex, int pixelIndex){
        if(tileMap != null){
            Tile tile = tileMap.get(tileIndex);
            if(tile != null){
                return tile.getPixel(rowIndex,pixelIndex);
            }
        }
        return PixelValue.ZERO;
    }

    public static Tile getTile(int tileIndex){
        if(tileMap != null) return tileMap.get(tileIndex);
        return null;
    }

    public static Collection<Tile> getTiles(){
        return tileMap.values();
    }

}
