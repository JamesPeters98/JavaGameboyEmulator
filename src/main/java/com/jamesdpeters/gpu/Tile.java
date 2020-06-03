package com.jamesdpeters.gpu;

public class Tile {

    int index;

    Tiles.PixelValue[][] pixels; //Row Index, Pixel Index

    public Tile(int index){
        this.index = index;
        pixels = new Tiles.PixelValue[8][8];
    }

    public void setPixel(int rowIndex, int pixelIndex, Tiles.PixelValue pixelValue){
        pixels[rowIndex][pixelIndex] = pixelValue;
    }

    public int getIndex(){
        return index;
    }

    public Tiles.PixelValue getPixel(int rowIndex, int pixelIndex){
        return pixels[rowIndex][pixelIndex];
    }

    public int[] getRGBArray(){
        int[] array = new int[64];
        int index = 0;
        for(Tiles.PixelValue[] row : pixels){
            for(Tiles.PixelValue pixel : row){
                array[index] = pixel.getRGB();
                index++;
            }
        }
        return array;
    }

}
