package com.jamesdpeters.gpu;

import com.jamesdpeters.Utils;

import java.util.Arrays;

public class Tile {

    private int index;
    private Tiles.PixelValue[][] pixels; //Row Index, Pixel Index

    public Tile(int index){
        this.index = index;
        pixels = new Tiles.PixelValue[getRows()][getCols()];
    }

    public void setPixel(int rowIndex, int pixelIndex, Tiles.PixelValue pixelValue){
        pixels[rowIndex][pixelIndex] = pixelValue;
    }

    public int getIndex(){
        return index;
    }

    public Tiles.PixelValue getPixel(int rowIndex, int pixelIndex){
        Tiles.PixelValue pixel = pixels[rowIndex][pixelIndex];
        if(pixel != null) return pixel;
        return Tiles.PixelValue.ZERO;
    }

    public int[] getRGBArray(){
        int[] array = new int[getCols()*getRows()];
        int index = 0;
        for(Tiles.PixelValue[] row : pixels){
            for(Tiles.PixelValue pixel : row){
                array[index] = pixel.getRGB();
                index++;
            }
        }
        return array;
    }

    int getRows(){
        return 8;
    }

    int getCols(){
        return 8;
    }

}
