package com.jamesdpeters.gpu;

import com.jamesdpeters.Utils;

import java.awt.*;

public enum PixelValue {
    ZERO(0), //00
    TWO(2), //10
    ONE(1), //01
    THREE(3); //11

    private int val;
    PixelValue(int val){
        this.val = val;
    }

    public enum Palette {
        BG,
        OBJ1,
        OBJ2
    }

    private Color bgColor = Color.WHITE, OBJ1 = Color.WHITE, OBJ2 = Color.WHITE;

    public Color getColor(Palette palette){
        switch (palette){
            case BG: return bgColor;
            case OBJ1: return OBJ1;
            case OBJ2: return OBJ2;
        }
        //Error color
        return Color.RED;
    }

    public void setColor(Palette palette, Color color){
        switch (palette){
            case BG:
                bgColor = color;
                break;
            case OBJ1:
                OBJ1 = color;
                break;
            case OBJ2:
                OBJ2 = color;
                break;
        }
    }

    /* STATIC METHODS */

    /** Cache background color palette **/
    public static void setBackgroundPalette(int BGP){
        for(PixelValue value : values()){
            value.setColor(Palette.BG,getColor(BGP,value.val));
        }
    }

    /** Cache OBJ 1 color palette **/
    public static void setOBJ1Palette(int OBJ){
        System.out.println("Setting OBJ1: "+ Utils.intToBinaryString(OBJ));
        for(PixelValue value : values()){
            value.setColor(Palette.OBJ1,getColor(OBJ,value.val));
        }
    }

    /** Cache OBJ 2 color palette **/
    public static void setOBJ2Palette(int OBJ){
        System.out.println("Setting OBJ2: "+ Utils.intToBinaryString(OBJ));
        for(PixelValue value : values()){
            value.setColor(Palette.OBJ2,getColor(OBJ,value.val));
        }
    }

    private static Color getColor(int bits, int pos){
        return getColor(getBits(bits,pos));
    }

    private static Color getColor(int color){
        switch (color){
            case 3: return PixelValue.BLACK;
            case 2: return PixelValue.DARK_GRAY;
            case 1: return PixelValue.LIGHT_GRAY;
            case 0: return PixelValue.WHITE;
            default: return PixelValue.COLOR_ERROR;
        }
    }

    /** Color - 0-3
        Returns 2 bits offset by pos.
        In the format 33221100.
     **/
    private static int getBits(int value, int pos){
        return (((1 << 2) - 1) & (value >> (2*pos)));
    }

    public static PixelValue getPixelValue(int lsb, int msb){
        if(lsb != 0 && msb != 0) return THREE;
        if(lsb == 0 && msb != 0) return TWO;
        if(lsb != 0) return ONE;
        return ZERO;
    }

    final static Color WHITE = new Color(255,255,255);
    final static Color LIGHT_GRAY = new Color(178,178,178);
    final static Color DARK_GRAY = new Color(102,102,102);
    final static Color BLACK = new Color(0,0,0);
    final static Color COLOR_ERROR = new Color(255,0,0);
}
