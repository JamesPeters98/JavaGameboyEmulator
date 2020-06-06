package com.jamesdpeters.gpu;

import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDValues;
import com.jamesdpeters.gpu.registers.LCDStatus;

public class GPU {

    private static int frameClock = 0;

    public static void step(int delta){
        frameClock += delta;

        int OAM_FRAME_TIME = 80;
        int TRANSFER_FRAME_TIME = 172;
        int HBLANK_FRAME_TIME = 204;
        int VBLANK_FRAME_TIME = 456;

        switch (LCDStatus.getMode()){
            case SEARCHING_OAM_RAM_2:
                if(frameClock >= OAM_FRAME_TIME){
                    frameClock -= OAM_FRAME_TIME;
                    LCDStatus.setMode(LCDStatus.Mode.TRANSFERRING_DATA_TO_LCD_3);
                }
                break;

            case TRANSFERRING_DATA_TO_LCD_3:
                if(frameClock >= TRANSFER_FRAME_TIME){
                    frameClock -= TRANSFER_FRAME_TIME;
                    LCDStatus.setMode(LCDStatus.Mode.HORIZONTAL_BLANK_PERIOD_0);

                    //TODO RENDERSCAN.
                }
                break;

            case HORIZONTAL_BLANK_PERIOD_0:
                if(frameClock >= HBLANK_FRAME_TIME){
                    frameClock -= HBLANK_FRAME_TIME;
                    LCDValues.incrementLineY();

                    if(LCDValues.getLineY() > 143){
                        //Enter VBlank
                        LCDStatus.setMode(LCDStatus.Mode.VERTICAL_BLANKING_PERIOD_1);
                        //TODO Write bytes to screen.
                    } else {
                        LCDStatus.setMode(LCDStatus.Mode.SEARCHING_OAM_RAM_2);
                    }
                }
                break;

            case VERTICAL_BLANKING_PERIOD_1:
                if(frameClock >= VBLANK_FRAME_TIME){
                    frameClock -= VBLANK_FRAME_TIME;
                    LCDValues.incrementLineY();

                    if(LCDValues.getLineY() > 153){
                        LCDStatus.setMode(LCDStatus.Mode.SEARCHING_OAM_RAM_2);
                        LCDValues.setLineY(0);
                    }
                }
                break;

            case INVALID_MODE:
                System.err.println("INVALID MODE in GPU");
                System.exit(-1);

        }
    }

    private static void renderScan(){
        //Which section of VRAM to use.
        int bgOffset = LCDControl.isBgCodeAreaSelection() ? 0x1C00 : 0x1800;

        bgOffset += ((LCDValues.getLineY() + LCDValues.getScrollY()) & 0xFF) >> 3;

        int lineOffset = LCDValues.getScrollX() >> 3;
    }
}
