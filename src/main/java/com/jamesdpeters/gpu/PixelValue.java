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

    private Color bgColor, OBJ1, OBJ2;
    public void setBgColor(Color bgColor){this.bgColor = bgColor;}
    public Color getBgColor() {
        return bgColor != null ? bgColor : WHITE;
    }

    public void setOBJ1(Color OBJ1) {
        this.OBJ1 = OBJ1;
    }
    public Color getOBJ1() {
        return OBJ1;
    }

    public void setOBJ2(Color OBJ2) {
        this.OBJ2 = OBJ2;
    }
    public Color getOBJ2() {
        return OBJ2;
    }

    /* STATIC METHODS */

    /** Cache background color palette **/
    public static void setBackgroundPalette(int BGP){
        for(PixelValue value : values()){
            value.setBgColor(getColor(getBits(BGP,value.val)));
        }
    }

    /** Cache OBJ 1 color palette **/
    public static void setOBJ1Palette(int OBJ){
        System.out.println("Setting OBJ1: "+ Utils.intToBinaryString(OBJ));
        for(PixelValue value : values()){
            value.setOBJ1(getColor(getBits(OBJ,value.val)));
        }
    }

    /** Cache OBJ 2 color palette **/
    public static void setOBJ2Palette(int OBJ){
        System.out.println("Setting OBJ2: "+ Utils.intToBinaryString(OBJ));
        for(PixelValue value : values()){
            value.setOBJ2(getColor(getBits(OBJ,value.val)));
        }
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
