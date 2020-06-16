package com.jamesdpeters.cpu;

import com.jamesdpeters.Utils;
import com.jamesdpeters.memory.MemoryBus;

public class DMATransfer {

    private static int startAddress;
    private static int offset;

    private static final int TRANSFER_TIME = 160; //Machine cycles.
    private static int currentTime;

    /**
     * @param address value of the FF46 register.
     */
    public static void start(int address){
        MemoryBus.isDMATransferActive = true;
        startAddress = (address & 0xFF) * 0x100;
        offset = 0;
    }

    public static void tick(int delta){
        if(MemoryBus.isDMATransferActive) {
            transfer(delta);
            currentTime += delta;
            if (currentTime >= TRANSFER_TIME * 4) {
                currentTime = 0;
                MemoryBus.isDMATransferActive = false;
            }
        }
    }

    private static void transfer(int delta){
        int cycles = delta/4;
        int start = startAddress+offset;
        for(int i=0; i<cycles; i++){
            int dest = offset+i;
            if(dest >= 160) break;
            int address = start+i;
            int mem = MemoryBus.getByteDuringDMA(address);
            MemoryBus.writeByteDuringDMA(MemoryBus.Bank.OAM.getStartAddress()+dest,mem);

//            System.out.println("OAM DMA Transfer: From: "+ Utils.intToString(address)+" = "+Utils.intToString(mem)+" To: "+Utils.intToString(MemoryBus.Bank.OAM.getStartAddress()+dest));
        }
        offset += cycles;
    }
}
