package com.jamesdpeters.gpu.registers;

import com.jamesdpeters.registers.ByteRegister;

import java.util.Arrays;

/**
 * LCD Status Register
 *
 *   Bit 6 - LYC=LY Coincidence Interrupt (1=Enable) (Read/Write)
 *   Bit 5 - Mode 2 OAM Interrupt         (1=Enable) (Read/Write)
 *   Bit 4 - Mode 1 V-Blank Interrupt     (1=Enable) (Read/Write)
 *   Bit 3 - Mode 0 H-Blank Interrupt     (1=Enable) (Read/Write)
 *   Bit 2 - Coincidence Flag  (0:LYC<>LY, 1:LYC=LY) (Read Only)
 *   Bit 1-0 - Mode Flag       (Mode 0-3, see below) (Read Only)
 *             0: During H-Blank
 *             1: During V-Blank
 *             2: During Searching OAM-RAM
 *             3: During Transferring Data to LCD Driver
 */
public class LCDStatus extends ByteRegister {

    /**
     * 00: Enable CPU access to all display RAM
     * 01: Vertical Blank
     * 10: Searching OAM RAM
     * 11: Transfer data to LCD driver
     **/
    public enum Mode {
        HORIZONTAL_BLANK_PERIOD_0,
        VERTICAL_BLANKING_PERIOD_1,
        SEARCHING_OAM_RAM_2,
        TRANSFERRING_DATA_TO_LCD_3,
        INVALID_MODE;
    }

    private static LCDStatus instance = new LCDStatus();

    public static void set(int b){
        instance.setByte(b);
//        System.out.println();
//        System.out.println("Set LCDControl: "+ Utils.intToBinaryString(b));
//        Utils.waitForInput();
    }

    @Override
    public int getAddress() {
        return 0xFF41;
    }

    public static Mode getMode(){
        if(doesMatchBits(true,true)) return Mode.TRANSFERRING_DATA_TO_LCD_3;
        if(doesMatchBits(true,false)) return Mode.SEARCHING_OAM_RAM_2;
        if(doesMatchBits(false, true)) return Mode.VERTICAL_BLANKING_PERIOD_1;
        if(doesMatchBits(false, false)) return Mode.HORIZONTAL_BLANK_PERIOD_0;
        return Mode.INVALID_MODE;
    }

    public static boolean isCoincidenceFlagSet(){
        return instance.bits[2];
    }

    public static boolean getHBlankIterrupt(){
        return instance.bits[3];
    }

    public static boolean getVBlankIterrupt(){
        return instance.bits[4];
    }

    public static boolean getOAMInterrupt(){
        return instance.bits[5];
    }

    public static boolean getCoincidenceInterrupt(){
        return instance.bits[6];
    }



    private static boolean doesMatchBits(boolean left, boolean right){
        return (instance.bits[1] == left) && (instance.bits[0] == right);
    }

    /**
     * SETTERS
     */

    public static void setMode(Mode Mode) {
        switch (Mode){
            case HORIZONTAL_BLANK_PERIOD_0:
                setModeBits(false,false);
                break;
            case VERTICAL_BLANKING_PERIOD_1:
                setModeBits(false,true);
                break;
            case SEARCHING_OAM_RAM_2:
                setModeBits(true,false);
                break;
            case TRANSFERRING_DATA_TO_LCD_3:
                setModeBits(true,true);
                break;
        }
        instance.pushRegister();
    }

    private static void setModeBits(boolean left, boolean right){
        instance.bits[0] = right;
        instance.bits[1] = left;
    }

    @Override
    public String toString() {
        return "LCDStatus{" +
                "bits=" + Arrays.toString(bits) +
                '}';
    }
}
