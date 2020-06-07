package com.jamesdpeters.gpu;

import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDValues;
import com.jamesdpeters.gpu.registers.LCDStatus;
import com.jamesdpeters.memory.MemoryBus;

public class GPU {

    private Display display;
    public GPU(Display display){
        this.display = display;
    }

    private static int frameClock = 0;
    private boolean isWaitingForFrame = false;

    public void step(int delta){
        frameClock += delta;

        int OAM_FRAME_TIME = 80;
        int TRANSFER_FRAME_TIME = 172;
        int HBLANK_FRAME_TIME = 204;
        int VBLANK_FRAME_TIME = 456;
        int TOTAL_FRAME_TIME = 70224;

//        System.out.println(LCDControl.getInstance());

        if(isWaitingForFrame && frameClock < TOTAL_FRAME_TIME){
            return;
        }
        if(!LCDControl.isLcdControllerOperation()){
            LCDValues.setLineY(0);
            isWaitingForFrame = true;
        }

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
                    renderScan();
                }
                break;

            case HORIZONTAL_BLANK_PERIOD_0:
                if(frameClock >= HBLANK_FRAME_TIME){
                    frameClock -= HBLANK_FRAME_TIME;
                    LCDValues.incrementLineY();

                    if(LCDValues.getLineY() > 143){
                        //Enter VBlank
                        LCDStatus.setMode(LCDStatus.Mode.VERTICAL_BLANKING_PERIOD_1);
                        display.draw();
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

    private void renderScan(){
        //Which section of VRAM to use.
        int bgOffset = LCDControl.isBgCodeAreaSelection() ? 0x1C00 : 0x1800;

        bgOffset += ((LCDValues.getLineY() + LCDValues.getScrollY()) & 0xFF) >> 3;

        int lineOffset = LCDValues.getScrollX() >> 3;

        int y = (LCDValues.getLineY() + LCDValues.getScrollY()) & 7;
        int x = LCDValues.getScrollX() & 7;

//        int canvasOffset = LCDValues.getLineY() * 160;

        int tile = MemoryBus.Bank.VRAM.getDirectByte(bgOffset + lineOffset)*16;

        if(LCDControl.isBgCharacterDataSelection() && tile < 0x80) tile += 0x100;

        for(int i=0; i < 160; i++){
//            System.out.println("Getting Pixel at x: "+x+" y: "+y+" tile: "+tile);
            Tiles.PixelValue pixelValue = Tiles.getTilePixel(tile,y,x);
            if(pixelValue == null) pixelValue = Tiles.PixelValue.ERROR;
            display
                    .setPixel(
                            LCDValues
                                    .getLineY(), i,
                            pixelValue
                                    .getRGB());

            x++;
            if(x == 8){
                x=0;
                lineOffset = (lineOffset + 1) & 0x1F;
                tile = MemoryBus.Bank.VRAM.getDirectByte(bgOffset + lineOffset);
                if(LCDControl.isBgCharacterDataSelection() && tile < 0x80) tile += 0x100;
            }
        }
    }
}
