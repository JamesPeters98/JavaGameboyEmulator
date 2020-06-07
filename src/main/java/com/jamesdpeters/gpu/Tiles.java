package com.jamesdpeters.gpu;

import java.awt.*;
import java.util.Collection;
import java.util.TreeMap;

public class Tiles {

    public enum PixelValue {
        ZERO, //00
        TWO, //10
        ONE, //01
        THREE, //11
        ERROR;

        private Color color = WHITE;
        public void setColor(Color color){this.color = color;}

        public Color getColor() {
            return color;
        }

        public int getRGB(){
            return color.getRGB();
        }
    }

    private final static Color WHITE = new Color(255,255,255);
    private final static Color LIGHT_GRAY = new Color(178,178,178);
    private final static Color DARK_GRAY = new Color(102,102,102);
    private final static Color BLACK = new Color(0,0,0);
    private final static Color ERROR = new Color(255,0,0);

    public static PixelValue getPixelValue(int lsb, int msb){
        if(lsb != 0 && msb != 0) return PixelValue.THREE;
        if(lsb == 0 && msb != 0) return PixelValue.TWO;
        if(lsb != 0 && msb == 0) return PixelValue.ONE;
        if(lsb == 0 && msb == 0) return PixelValue.ZERO;
        return PixelValue.ERROR;
    }

    private static Color getColor(int color){
        switch (color){
            case 3: return BLACK;
            case 2: return DARK_GRAY;
            case 1: return LIGHT_GRAY;
            case 0: return WHITE;
            default: return ERROR;
        }
    }

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

    public static Collection<Tile> getTiles(){
        return tileMap.values();
    }

    /** Cache color palette **/
    public static void setColorPalette(int BGP){
        PixelValue.ZERO.setColor(getColor(getBits(BGP,0)));
        PixelValue.ONE.setColor(getColor(getBits(BGP,1)));
        PixelValue.TWO.setColor(getColor(getBits(BGP,2)));
        PixelValue.THREE.setColor(getColor(getBits(BGP,3)));
    }

    /** Color - 0-3 **/
    private static int getBits(int value, int pos){
        return (((1 << 2) - 1) & (value >> (2*pos)));
    }

}
