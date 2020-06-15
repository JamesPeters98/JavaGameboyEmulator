package com.jamesdpeters.gpu;

public class Tile {

    private int index;
    private PixelValue[][] pixels; //Row Index, Pixel Index

    public Tile(int index){
        this.index = index;
        pixels = new PixelValue[getRows()][getCols()];
    }

    public void setPixel(int rowIndex, int pixelIndex, PixelValue pixelValue){
        pixels[rowIndex][pixelIndex] = pixelValue;
    }

    public int getIndex(){
        return index;
    }

    public PixelValue getPixel(int rowIndex, int pixelIndex){
        PixelValue pixel = pixels[rowIndex][pixelIndex];
        if(pixel != null) return pixel;
        return PixelValue.ZERO;
    }

    public int[] getRGBArray(){
        int[] array = new int[getCols()*getRows()];
        int index = 0;
        for(PixelValue[] row : pixels){
            for(PixelValue pixel : row){
                if(pixel == null) array[index] = PixelValue.ZERO.getBgColor().getRGB();
                else array[index] = pixel.getBgColor().getRGB();
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
