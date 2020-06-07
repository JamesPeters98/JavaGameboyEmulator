package com.jamesdpeters.gpu;

import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Display extends Canvas implements Runnable {

    JFrame frame;
//    JPanel panel;
    BufferedImage image;
    CPU cpu;
    Thread thread;


    public final double FPS = 59.73;
    public final double FRAME = TimeUnit.SECONDS.toNanos(1)/FPS;
    public int WIDTH = 160;
    public int HEIGHT = 144;

    private long cpuTime = 0;
    private long lastTime = 0;
    private String title;

    private double currentFPS;

    public int[] pixels;

    public Display(CPU cpu){
        this.cpu = cpu;

        frame = new JFrame();
//        frame.setUndecorated(true);
//        frame.getRootPane().setWindowDecorationStyle( JRootPane. FRAME );

//        panel = new JPanel();
        setDimensions(WIDTH,HEIGHT);
//        frame.add(panel);
        frame.add(this);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        setTitle(cpu.getCart().getTitle());

//        panel.getGraphics().drawImage(image,0,0,WIDTH,HEIGHT,null);
    }

    public void setDimensions(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
        Dimension d = new Dimension(3*WIDTH,3*HEIGHT);
//        panel.setSize(d);
        setPreferredSize(d);

        image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = new int[width*height];
    }

    public void setTitle(String title){
        this.title = title;
        frame.setTitle(title);
    }

    public void setPixel(int row, int col, int colour){
        pixels[row*WIDTH+col] = colour;
    }

    public void setPixels(int[] pixels){
        image.setRGB(0,0, WIDTH,HEIGHT,pixels,0,WIDTH);
    }

    public void setTile(int rowIndex, int colIndex, Tile tile){
        int[] pixels = tile.getRGBArray();
        image.setRGB(colIndex*8, rowIndex*8, 8, 8, pixels, 0, 8);
    }

    public void draw(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(4);
            return;
        }
        bs.show();

        Graphics g = bs.getDrawGraphics();
        setPixels(pixels);
        g.drawImage(image,0,0,getWidth(),getHeight(),null);
        g.dispose();
//        setPixels(pixels);
//        Image resized = image.getScaledInstance(panel.getWidth(),panel.getHeight(),Image.SCALE_DEFAULT);
//        panel.getGraphics().drawImage(resized,0,0,panel.getWidth(),panel.getHeight(),null);
    }

    private void tick(){
        long delta = delta();
        cpuTime += delta;
        if(cpuTime >= FRAME){
            cpuTime = 0;
            draw();
        }
    }

    private long delta(){
        long current = System.nanoTime();
        long result = (current - lastTime);
        lastTime = current;
        return result;
    }

    public void start(){
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true){
            tick();
        }
    }
}
