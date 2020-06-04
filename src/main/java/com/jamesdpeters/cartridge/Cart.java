package com.jamesdpeters.cartridge;

import com.jamesdpeters.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

public class Cart {

    private static ClassLoader classLoader = Cart.class.getClassLoader();

    private static int[] titleRange = new int[]{0x0134,0x0143};
    static int entryPoint = 0x0100;

    public int[] rom;

    public Cart(String romName){
        try {
            File file = new File(classLoader.getResource(romName).getFile());
            byte[] rom = Files.readAllBytes(file.toPath());
            this.rom = Utils.ByteToInt(rom);
            System.out.println("Loading ROM: "+getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getTitle(){
        int[] title = Arrays.copyOfRange(rom,titleRange[0],titleRange[1]);
        return new String(Utils.IntToByte(title), StandardCharsets.UTF_8);
    }




}
