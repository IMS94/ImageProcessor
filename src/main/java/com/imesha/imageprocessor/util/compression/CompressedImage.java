package com.imesha.imageprocessor.util.compression;

/*
 * @author imesha
 * @date 8/5/16.
 */

public class CompressedImage {
    private String binaryRepresentation;
    private int width, height;

    public CompressedImage(int width, int height, String binaryRepresentation) {
        this.binaryRepresentation = binaryRepresentation;
        this.width = width;
        this.height = height;
    }

    public String getBinaryRepresentation() {
        return binaryRepresentation;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
