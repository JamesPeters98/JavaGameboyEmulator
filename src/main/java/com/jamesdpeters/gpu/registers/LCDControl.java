package com.jamesdpeters.gpu.registers;

import com.jamesdpeters.registers.ByteRegister;

public class LCDControl extends ByteRegister {

    private static LCDControl instance = new LCDControl();

    public static void set(int b){
        instance.setByte(b);
//        System.out.println();
//        System.out.println("Set LCDControl: "+ Utils.intToBinaryString(b));
//        System.out.println(instance);
//        Utils.waitForInput();
    }

    @Override
    public int getAddress() {
        return 0xFF40;
    }

    /** TRUE: BG Display ON;    FALSE: BG Display OFF **/
    public static void setBgDisplay(boolean bgDisplay) {
        instance.bits[0] = bgDisplay;
        instance.pushRegister();
    }

    /** TRUE: ON;               FALSE: OFF **/
    public static void setObjOn(boolean objOn) {
        instance.bits[1] = objOn;
        instance.pushRegister();
    }

    /** TRUE: 8x16 dots;        FALSE: 8x8 dots **/
    public static void set8x16SpriteSize(boolean spriteSize) {
        instance.bits[2] = spriteSize;
        instance.pushRegister();
    }

    /** TRUE: 0x9C00-0x9FFF;    FALSE: 0x9800-0x9BFF **/
    public static void setBgCodeAreaSelection(boolean bgCodeAreaSelection) {
        instance.bits[3] = bgCodeAreaSelection;
        instance.pushRegister();
    }

    /** TRUE: 0x8000-0x8FFF;    FALSE: 0x8800-0x8BFF **/
    public static void setBgCharacterDataSelection(boolean bgCharacterDataSelection) {
        instance.bits[4] = bgCharacterDataSelection;
        instance.pushRegister();
    }

    /** TRUE: ON;               FALSE: OFF **/
    public static void setWindowDisplayEnabled(boolean windowing) {
        instance.bits[5] = windowing;
        instance.pushRegister();
    }

    /** TRUE: 0x9C00-0x9FFF;    FALSE: 0x9800-0x9BFF **/
    public static void setWindowingCodeAreaSelection(boolean windowingCodeAreaSelection) {
        instance.bits[6] = windowingCodeAreaSelection;
        instance.pushRegister();
    }

    /** TRUE: LCDControl ON;          FALSE: LCDControl OFF **/
    public static void setLcdControllerOperation(boolean lcdControllerOperation) {
        instance.bits[7] = lcdControllerOperation;
        instance.pushRegister();
    }

    public static boolean isBgDisplay() {
        return instance.bits[0];
    }
    public static boolean isObjOn() {
        return instance.bits[1];
    }
    public static boolean is8x16SpriteSize() {
        return instance.bits[2];
    }
    public static boolean isBgCodeAreaSelection() {
        return instance.bits[3];
    }
    public static boolean isBgCharacterDataSelection() {
        return instance.bits[4];
    }
    public static boolean isWindowDisplayEnabled() {
        return instance.bits[5];
    }
    public static boolean isWindowingCodeAreaSelection() {
        return instance.bits[6];
    }
    public static boolean isLCDDisplayEnabled() {
        return instance.bits[7];
    }

    @Override
    public String toString() {
        return "LCDControl{" +
                "BG_DISPLAY=" + isBgDisplay() +
                ", OBJ_ON=" + isObjOn() +
                ", OBJ_BLOCK_COMPOSITION_SELECTION=" + is8x16SpriteSize() +
                ", BG_CODE_AREA_SELECTION=" + isBgCodeAreaSelection() +
                ", BG_CHARACTER_DATA_SELECTION=" + isBgCharacterDataSelection() +
                ", WINDOWING=" + isWindowDisplayEnabled() +
                ", WINDOWING_CODE_AREA_SELECTION=" + isWindowingCodeAreaSelection() +
                ", LCD_CONTROLLER_OPERATION=" + isLCDDisplayEnabled() +
                '}';
    }

    public static LCDControl getInstance() {
        return instance;
    }
}
