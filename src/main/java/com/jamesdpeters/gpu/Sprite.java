package com.jamesdpeters.gpu;

import com.jamesdpeters.Utils;
import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDValues;
import com.jamesdpeters.monitoring.SpriteWindow;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;

public class Sprite {

    private static Sprite[] sprites = new Sprite[40];

    public static Sprite[] getSprites() {
        return sprites;
    }

    /**
     * Set a specific byte in OAM.
     * @param address offset from 0xFE00.
     * @param b byte.
     */
    public static void setOAM(int address, int b){
        int index = address / 4;
        Sprite sprite = sprites[index];
        if(sprite == null){
            sprite = new Sprite();
            sprite.OAMIndex = index;
            sprites[index] = sprite;

//            SpriteWindow.listView.refresh();
            Platform.runLater(() -> {
                SpriteWindow.data.clear();
                SpriteWindow.data.addAll(sprites);
            });
        }
        int bytePos = address % 4;

        int oldByte = sprite.bytes[bytePos];
        //Don't change byte if it's the same.
        if(oldByte == b){
            return;
        }
//        System.out.println("Previous Sprite: "+sprite);
        sprite.bytes[bytePos] = b;
        SpriteWindow.listView.refresh();
//        System.out.println("     New Sprite: "+sprite);


//        System.out.println(sprite);
    }

    public static Sprite getSprite(int index){
        return sprites[index];
    }

    private int[] bytes = new int[4];
    private int OAMIndex;

    public int getIndex(){
        return OAMIndex;
    }

    /**
     * @return Specifies the sprites vertical position on the screen (minus 16).
     * An off-screen value (for example, Y=0 or Y>=160) hides the sprite.
     */
    public int getYPosition(){
        return bytes[0]-16;
    }

    /**
     * @return Specifies the sprites horizontal position on the screen (minus 8).
     * An off-screen value (X=0 or X>=168) hides the sprite,
     * but the sprite still affects the priority ordering
     * - a better way to hide a sprite is to set its Y-coordinate off-screen.
     */
    public int getXPosition(){
        return bytes[1]-8;
    }

    /**
     * @return Specifies the sprites Tile Number (00-FF).
     * This (unsigned) value selects a tile from memory at 8000h-8FFFh.
     * In 8x16 mode, the lower bit of the tile number is ignored.
     * IE: the upper 8x8 tile is "NN AND FEh", and the lower 8x8 tile is "NN OR 01h".
     */
    public int getTileNumber(){
        return bytes[2];
    }

    public int getAttributes(){
        return bytes[3];
    }

    /**
     * True = OBJ Behind BG.
     * False = OBJ above BG.
     * @return
     */
    public boolean getOBJtoBGPriority(){
        return Utils.getBit(getAttributes(),7) == 1;
    }

    public boolean isYFlipped(){
        return Utils.getBit(getAttributes(),6) == 1;
    }

    public boolean isXFlipped(){
        return Utils.getBit(getAttributes(),5) == 1;
    }

    public boolean getPaletteNumber(){
        return Utils.getBit(getAttributes(),4) == 1;
    }

    public PixelValue.Palette getPalette(){
        return getPaletteNumber() ? PixelValue.Palette.OBJ2 : PixelValue.Palette.OBJ1;
    }


    /*
    HELPER METHODS
     */

    public Tile getTile(){
        return Tiles.getTile(getTileNumber());
    }

    public boolean isSpriteInCurrentRenderScan(){
        return (getYPosition() <= LCDValues.getLineY()) && (getYPosition()+getSpriteHeight() > LCDValues.getLineY());
    }

    public int getSpriteHeight(){
        return LCDControl.is8x16SpriteSize() ? 16 : 8;
    }

    public PixelValue[] getSpritePixelRow(){
        int pos = (LCDValues.getLineY()-getYPosition());
        int index = isYFlipped() ? (7-pos) : pos;
        return getTile().getTileRow(index);
    }

    /**
     * Checks if the given sprite pixel is on the screen.
     * @param x - pixel row offset.
     */
    public boolean isSpritePixelOnScreen(int x){
        boolean bool = (getXPosition()+x) >= 0
                && (getXPosition()+x) < 160;
//        if(bool) System.out.println("Sprite: "+getIndex()+" pixel: "+x+" is on screen!");

        return bool;
    }

//    public boolean isPixelRowTransparent(boolean yFlip){
//
//    }


    @Override
    public String toString() {
        return "Sprite[Index="+getIndex()+" Y="+getYPosition()+" X="+getXPosition()+" Tile Number="+getTileNumber()+"]";
    }

}
