package com.jamesdpeters.gpu;

import com.jamesdpeters.Utils;
import com.jamesdpeters.gpu.registers.LCDControl;
import com.jamesdpeters.gpu.registers.LCDValues;
import com.jamesdpeters.gpu.registers.LCDStatus;
import com.jamesdpeters.memory.MemoryBus;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

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
//                        display.tick();
//                        display.draw();
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
        int bgOffset = LCDControl.isBgCodeAreaSelection() ? 0x9C00 : 0x9800;

        int bgX = LCDValues.getScrollX() / 0x08;
        int bgY = (LCDValues.getScrollY() + LCDValues.getLineY()) % 0x100;
        int mapAddress = bgOffset + (bgY / 0x08) * 0x20;

        int tile = MemoryBus.getByte(mapAddress+bgX);
//        System.out.println("MapAdr: "+Utils.intToString(mapAddress) + " bgX: "+bgX+" bgY: "+bgY+" Line Y: "+LCDValues.getLineY());
        int line = bgY % 0x08;

        boolean isTileIDSigned = LCDControl.isBgCharacterDataSelection();
//        int tileDataAddress = isTileIDSigned ? 0 : 0x1000;
        if(!isTileIDSigned) tile += 256;

        int x = LCDValues.getScrollX() & 7;

//        int tileData1 = getTile(tile,line,0, tileDataAddress, isTileIDSigned);
//        int tileData2 = getTile(tile,line,1, tileDataAddress, isTileIDSigned);

        for(int i=0; i < 160; i++){
//            System.out.println("Getting tile: "+tile+" line: "+line+" tileX: "+x+" bgY: "+bgY);
            Tiles.PixelValue pixelValue = Tiles.getTilePixel(tile,line,x);
//            System.out.print("| tile="+tile+" line="+line+" x="+x+" ");
            if(pixelValue == null) {
                System.err.println("NULL tile: "+tile+" line: "+line+" tileX: "+x+" bgY: "+bgY);
            }
            display.setPixel(
                            LCDValues.getLineY(), i,
                            pixelValue.getRGB());

            x++;
            if(x == 8){
                x=0;
                bgX = (bgX + 1) & 0x1F;
                int adr = mapAddress + bgX;
                tile = MemoryBus.getByte(adr);

//                System.out.println("Next Tile: "+Utils.intToString(adr));
                if(adr >= 0x9900 && adr <= 0x9930){
//                    System.out.println("Adr: "+Utils.intToString(adr)+" Tile: "+tile);
//                    Utils.waitForInput();
                }
//                if(LCDControl.isBgCharacterDataSelection() && tile < 0x80) tile += 0x100;
            }
        }
//        if(LCDControl.isLcdControllerOperation()) Utils.waitForInput();
    }

    private int getTile(int tileId, int line, int byteOffset, int tileDataAddress, boolean signed){
        int tileIndex;
        if(signed){
            tileIndex = tileDataAddress + toSigned(tileId) * 0x10;
        } else {
            tileIndex = tileDataAddress + tileId * 0x10;
        }

//        Tile tile = Tiles.get

        return MemoryBus.getByte(tileIndex + line * 2 + byteOffset);
    }

    public static int toSigned(int byteValue) {
        if ((byteValue & (1 << 7)) == 0) {
            return byteValue;
        } else {
            return byteValue - 0x100;
        }
    }

    private void renderScanOld(){
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
