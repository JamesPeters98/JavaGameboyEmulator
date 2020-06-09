package com.jamesdpeters.gpu;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDValues;
import com.jamesdpeters.gpu.registers.LCDStatus;
import com.jamesdpeters.memory.MemoryBus;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.concurrent.locks.LockSupport;

public class GPU {

    private final int OAM_FRAME_TIME = 80;
    private final int TRANSFER_FRAME_TIME = 172;
    private final int HBLANK_FRAME_TIME = 204;
    private final int VBLANK_FRAME_TIME = 456;
    private final int TOTAL_FRAME_TIME = 70224;

    private final int CLOCKSPEED = 4194304; /** Clockspeed of Gameboy in Hz **/
    private final double FRAME_TIME = Math.pow(10,9)*TOTAL_FRAME_TIME/CLOCKSPEED; /** Amount of time each frame should take in nano seconds. **/

    private long lastFrameTime;

    private Display display;
    public GPU(Display display){
        this.display = display;
    }

    private static int frameClock = 0;
    private boolean isWaitingForFrame = false;

    public void step(int delta){
        frameClock += delta;

        if(isWaitingForFrame && frameClock < TOTAL_FRAME_TIME){
            return;
        }
//        else if(isWaitingForFrame){
//            syncClockSpeed();
//            isWaitingForFrame = false;
//        }
        if(!LCDControl.isLcdControllerOperation()){
            LCDValues.setLineY(0);
            lastFrameTime = System.nanoTime();
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
                        display.run();
                    } else {
                        LCDStatus.setMode(LCDStatus.Mode.SEARCHING_OAM_RAM_2);
                    }
                }
                break;

            case VERTICAL_BLANKING_PERIOD_1:
                if(frameClock >= VBLANK_FRAME_TIME){
                    LCDValues.incrementLineY();

                    if(LCDValues.getLineY() > 153){
                        LCDStatus.setMode(LCDStatus.Mode.SEARCHING_OAM_RAM_2);
                        LCDValues.setLineY(0);
                        syncClockSpeed();
                    }
                    frameClock -= VBLANK_FRAME_TIME;
                }
                break;

            case INVALID_MODE:
                System.err.println("INVALID MODE in GPU");
                System.exit(-1);

        }
    }

    private void renderScan(){
        //Which section of VRAM to use.
        int bgOffset = LCDControl.isBgCodeAreaSelection() ? 0x9C00 : 0x9800;

        int bgX = LCDValues.getScrollX() / 0x08;
        int bgY = (LCDValues.getScrollY() + LCDValues.getLineY()) % 0x100;
        int mapAddress = bgOffset + (bgY / 0x08) * 0x20;

        int tile = MemoryBus.getByte(mapAddress+bgX);
        int line = bgY % 0x08;

        boolean isTileIDSigned = LCDControl.isBgCharacterDataSelection();
        if(!isTileIDSigned) tile += 256;

        int x = LCDValues.getScrollX() & 7;

        for(int i=0; i < 160; i++){
            Tiles.PixelValue pixelValue = Tiles.getTilePixel(tile,line,x);
            if(pixelValue == null) { pixelValue = Tiles.PixelValue.ERROR; }
            display.setPixel(LCDValues.getLineY(), i, pixelValue.getRGB());

            x++;
            if(x == 8){
                x=0;
                bgX = (bgX + 1) & 0x1F;
                int adr = mapAddress + bgX;
                tile = MemoryBus.getByte(adr);

//                if(LCDControl.isBgCharacterDataSelection() && tile < 0x80) tile += 0x100;
            }
        }
    }

    private void syncClockSpeed(){
//        System.out.println("New Frame: clock="+frameClock);
        long clockTime = Math.round(Math.pow(10,9)*((double) frameClock/CLOCKSPEED));

        long now = System.nanoTime();
        long frameTime = now - lastFrameTime;
        if(frameTime < clockTime){
            long time = (clockTime-frameTime);

            busyWaitMicros(time);
//            System.out.println("Sleeping: "+time+" ms");
//            try {
//                Thread.sleep(0, (int) Math.round(time));
//                LockSupport.parkNanos(time+1000000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }
        lastFrameTime = System.nanoTime();
    }

    private static void busyWaitMicros(long nano){
        long waitUntil = System.nanoTime() + nano;
        while(waitUntil > System.nanoTime()){
            ;
        }
    }


}
