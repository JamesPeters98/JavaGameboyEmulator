package com.jamesdpeters;

import com.jamesdpeters.memory.MemoryBus;
import com.jamesdpeters.registers.ByteRegister;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Joypad extends ByteRegister implements KeyListener {

    public static Joypad instance = new Joypad();

    public enum Button {
        START(5,3),
        SELECT(5, 2),
        A(5, 1),
        B(5,0),

        UP(4,3),
        DOWN(4,2),
        LEFT(4,1),
        RIGHT(4,0);

        private int selectBit, pressedBit;
        private boolean pressed = false;
        Button(int selectBit, int pressedBit){
            this.selectBit = selectBit;
            this.pressedBit = pressedBit;
        }

        private static int currentSelectedBit = 5;

        void press(){
            pressed = true;
        }

        void release(){
            pressed = false;
        }

        static boolean[] getBits(){
            boolean[] bits = new boolean[8];
            bits[7] = true;
            bits[6] = true;
            bits[currentSelectedBit] = true;
            for(Button button : values()){
                if(button.selectBit == currentSelectedBit){
                    bits[button.pressedBit] = !button.pressed;
                }
            }
            return bits;
        }

        static void setCurrentSelectedBit(int bit){
            currentSelectedBit = bit;
        }
    }

    //Push initial values to Joypad register.
    public static void init(){
        pushBits();
    }

    public static void set(int byte_){
        if(Utils.getBit(byte_,5) == 1) Button.setCurrentSelectedBit(5);
        else if(Utils.getBit(byte_,4) == 1) Button.setCurrentSelectedBit(4);
        pushBits();
    }

    private static void pushBits(){
        instance.bits = Button.getBits();
        instance.updateMemory();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.out.println("Key Released!");
    }

    @Override
    public int getAddress() {
        return 0xFF00;
    }

    private void updateMemory(){
        int byte_ = 0;
        for(int b=0; b<8; b++){
            byte_ = Utils.setBit(byte_,b,bits[b]);
        }
        MemoryBus.Bank.IO_REGISTERS.setDirectByte(0, byte_);
    }
}
