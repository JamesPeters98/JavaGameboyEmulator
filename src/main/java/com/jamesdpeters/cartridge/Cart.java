package com.jamesdpeters.cartridge;

import com.jamesdpeters.memory.MemoryBus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Cart {

    private static ClassLoader classLoader = Cart.class.getClassLoader();

    static int[] titleRange = new int[]{0x0134,0x0143};
    static int entryPoint = 0x0100;

    public MemoryBus rom;

    public Cart(String romName){
        try {
            File file = new File(classLoader.getResource(romName).getFile());
            byte[] rom = Files.readAllBytes(file.toPath());
            this.rom = new MemoryBus(rom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTitle(){
        byte[] title = this.rom.getBytes(MemoryBus.Bank.ROM,titleRange[0],titleRange[1]);
        return new String(title, StandardCharsets.UTF_8);
    }


}
