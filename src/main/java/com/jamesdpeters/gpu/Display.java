package com.jamesdpeters.gpu;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Joypad;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.monitoring.Monitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class Display extends Canvas implements Runnable {

    JFrame frame;
    BufferedImage image;
    CPU cpu;


    public final double FPS = 59.73;
    public final double FRAME = TimeUnit.SECONDS.toNanos(1)/FPS;
    public int WIDTH = 160;
    public int HEIGHT = 144;

    private int TILE_ROWS;

    private long cpuTime = 0;
    private long lastTime = 0;
    private String title;

    private double currentFPS;

    public int[] pixels;

    public Display(CPU cpu){
        init(WIDTH,HEIGHT);
        this.cpu = cpu;
        title = cpu.getCart().getTitle();
        addKeyListener(Joypad.instance);
    }


    public Display(int width, int height){
        init(width, height);
        pixels = null;
    }

    public static Display BACKGROUND_TILE_DISPLAY(){
        return new Display(200,200);
    }

    private void init(int width, int height){
        frame = new JFrame();
        setDimensions(width, height);
        frame.add(this);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
//                GameBoy.instance.running = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {
//                Monitor.saveToCSV();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public void setDimensions(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
        this.TILE_ROWS = (int) Math.floor(WIDTH/8);
        Dimension d = new Dimension(3*WIDTH,3*HEIGHT);
        setPreferredSize(d);

        image = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = new int[width*height];
    }

    public void setTitle(String title){
        this.title = title;
        frame.setTitle(title);
    }

    //Set FPS in title
    public void setFPS(double fps){
        frame.setTitle(title+" FPS: " + Math.round(fps));
    }

    public void setPixel(int row, int col, int colour){
        pixels[row*WIDTH+col] = colour;
    }

    public void setRowPixels(int row, int[] pixels){
        System.arraycopy(pixels, 0, this.pixels, row*WIDTH, pixels.length);
    }

    public void setPixels(int[] pixels){
        image.setRGB(0,0, WIDTH,HEIGHT,pixels,0,WIDTH);
    }

    public void setTile(int rowIndex, int colIndex, Tile tile){
        int[] pixels = tile.getRGBArray(PixelValue.Palette.BG);
        image.setRGB(colIndex*8, rowIndex*8, 8, 8, pixels, 0, 8);
    }

    public void setTile(Tile tile){
        int row = (tile.getIndex() / TILE_ROWS);
        int col = tile.getIndex() % TILE_ROWS;
        setTile(row,col,tile);
//        draw();
    }

    private void draw(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(4);
            return;
        }
        bs.show();

        Graphics g = bs.getDrawGraphics();
        if(pixels != null) setPixels(pixels);
        g.setColor(Color.WHITE);
        g.drawImage(image,0,0,getWidth(),getHeight(),null);
        g.dispose();
    }

//    private void tick(){
//        long delta = delta();
//        cpuTime += delta;
//        if(cpuTime >= FRAME){
//            cpuTime = 0;
//            draw();
//        }
//    }

    private long delta(){
        long current = System.nanoTime();
        long result = (current - lastTime);
        lastTime = current;
        return result;
    }

//    public void start(){
//        thread = new Thread(this);
//        thread.start();
//    }

    @Override
    public void run() {
        draw();
        double FPS = Math.pow(10,9)/delta();
        setFPS(FPS);
//        System.out.println("FPS: "+FPS);
//        while(true){
//            tick();
//        }
    }
}
