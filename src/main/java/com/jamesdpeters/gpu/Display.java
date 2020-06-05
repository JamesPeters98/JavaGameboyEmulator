package com.jamesdpeters.gpu;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class Display {

    JFrame frame;
    JPanel panel;
    BufferedImage image;

    public final double FPS = 59.73;
    public final double FRAME = TimeUnit.SECONDS.toNanos(1)/FPS;
    public final int WIDTH = 160;
    public final int HEIGHT = 144;

    private long cpuTime = 0;
    private long lastTime = 0;

    private double currentFPS;

    public Display(){
        frame = new JFrame();
        panel = new JPanel();

        image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);

        Dimension d = new Dimension(3*WIDTH,3*HEIGHT);
        frame.setSize(d);
        frame.setPreferredSize(d);
        frame.add(panel);


        frame.setResizable(true);
        frame.setVisible(true);
        frame.pack();

        panel.getGraphics().drawImage(image,0,0,WIDTH,HEIGHT,null);
    }

    public void setPixel(int row, int col, int colour){
        image.setRGB(col,row,colour);
        draw();
    }

    public void setPixels(int[] pixels){
        image.setRGB(0,0, WIDTH,HEIGHT,pixels,0,WIDTH);
        draw();
    }

    public void setTile(int rowIndex, int colIndex, Tile tile){
        //System.out.println("Setting tile: "+rowIndex+","+colIndex);
        int[] pixels = tile.getRGBArray();
        image.setRGB(colIndex*8, rowIndex*8, 8, 8, pixels, 0, 8);
    }

    public void draw(){
        Image resized = image.getScaledInstance(frame.getWidth(),frame.getHeight(),Image.SCALE_DEFAULT);
        panel.getGraphics().drawImage(resized,0,0,frame.getWidth(),frame.getHeight(),null);
    }

    public void tick(){
        long delta = delta();
        cpuTime += delta;
        if(cpuTime >= FRAME){
            cpuTime = 0;
            draw();
        }
    }

//    private void runFrame(){
//        Random random = new Random();
//
//        int[] pixels = new int[HEIGHT*WIDTH];
//        for(int pixel=0; pixel < pixels.length; pixel++){
//            pixels[pixel] = random.nextInt(16777216);
//        }
//
//        setPixels(pixels);
//    }

    private long delta(){
        long current = System.nanoTime();
        long result = (current - lastTime);
        lastTime = current;
        return result;
    }

}
