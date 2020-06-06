package com.jamesdpeters.gpu;

public class BackgroundTile extends Tile {

    public BackgroundTile(int index) {
        super(index);
    }

    @Override
    int getCols() {
        return 32;
    }

    @Override
    int getRows() {
        return 32;
    }
}
