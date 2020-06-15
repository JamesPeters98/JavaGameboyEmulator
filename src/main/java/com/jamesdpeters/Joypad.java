package com.jamesdpeters;

import com.jamesdpeters.cpu.Interrupts;
import com.jamesdpeters.memory.MemoryBus;
import com.jamesdpeters.registers.ByteRegister;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Joypad extends ByteRegister implements KeyListener {

    public static Joypad instance = new Joypad();

    public enum Button {
        START(5,3, KeyEvent.VK_SHIFT),
        SELECT(5, 2, KeyEvent.VK_CONTROL),
        A(5, 1, KeyEvent.VK_ENTER),
        B(5,0, KeyEvent.VK_DECIMAL),

        UP(4,3, KeyEvent.VK_UP),
        DOWN(4,2, KeyEvent.VK_DOWN),
        LEFT(4,1, KeyEvent.VK_LEFT),
        RIGHT(4,0, KeyEvent.VK_RIGHT);

        private int selectBit, pressedBit;
        private boolean pressed = false;
        private int keyCode;
        Button(int selectBit, int pressedBit, int defaultKeyCode){
            this.selectBit = selectBit;
            this.pressedBit = pressedBit;
            this.keyCode = defaultKeyCode;
        }

        private static boolean buttonSelected = true, directionSelected = false;

        public static void setButtonSelected(boolean buttonSelected) {
            Button.buttonSelected = buttonSelected;
        }

        public static void setDirectionSelected(boolean directionSelected) {
            Button.directionSelected = directionSelected;
        }

        static boolean DEBUG_PRESS;

        void press(){
            pressed = true;
            pushBits();
            if(isSelected()) Interrupts.JOYPAD.request();

//            if(A.pressed && B.pressed && START.pressed && SELECT.pressed){
//                System.out.println("Resetting Tetris!");
//                GameBoy.instance.debugStep = true;
//                DEBUG_PRESS = true;
//            }
        }

        void release(){
            pressed = false;
            pushBits();
        }

        boolean isSelected(){
            return (selectBit == 5 && buttonSelected) || (selectBit == 4 && directionSelected);
        }

        public int getKeyCode() {
            return keyCode;
        }

        /**
         * Set the Key code for the given button.
         * @param keyCode use @{@link KeyEvent} codes.
         */
        public Button setKeyCode(int keyCode) {
            this.keyCode = keyCode;
            return this;
        }

        static boolean simultaneousSelect(){
            return buttonSelected && directionSelected;
        }

        static Button getButton(int keyCode){
            for(Button button : values()){
                if(button.getKeyCode() == keyCode) return button;
            }
            return null;
        }

        static boolean[] getBits(){
            boolean[] bits = new boolean[8];
            bits[7] = true;
            bits[6] = true;
            bits[5] = !buttonSelected;
            bits[4] = !directionSelected;
            for(Button button : values()){
                if(button.isSelected()){
                    if(!bits[button.pressedBit] && Button.simultaneousSelect()) continue;
                    bits[button.pressedBit] = !button.pressed;
                }
            }
            return bits;
        }
    }

    //Push initial values to Joypad register.
    public static void init(){
        pushBits();
    }

    public static void set(int byte_){
        Button.setButtonSelected(Utils.getBit(byte_,5) == 0);
        Button.setDirectionSelected(Utils.getBit(byte_,4) == 0);
        pushBits();
    }

    private static void pushBits(){
        instance.bits = Button.getBits();
        instance.updateMemory();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Not needed
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Button button = Button.getButton(e.getKeyCode());
        if(button != null){
            button.press();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Button button = Button.getButton(e.getKeyCode());
        if (button != null) {
            button.release();
        }
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
