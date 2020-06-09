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
    private byte[] byteRom;

    public Cart(String romName){
        try {
            File file = new File(classLoader.getResource(romName).getFile());
            byteRom = Files.readAllBytes(file.toPath());
            rom = Utils.ByteToInt(byteRom);
            System.out.println("Loading ROM: "+getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle(){
        byte[] title = Arrays.copyOfRange(byteRom,titleRange[0],titleRange[1]);
        return new String(title, StandardCharsets.UTF_8).trim();
    }




}
