package com.imesha.imageprocessor.util.compression;
/*
 * @author imesha
 * @date 8/7/16.
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class BitPlaneCodingCompressor implements ImageCompressor {

    private static final String ROW_SEPERATOR = "\n";
    private static final String BIT_SEPERATOR = ":";
    private static final String CHARACTER_SEPERATOR = ",";


    /**
     * Compresses the image using bit plane coding
     *
     * @param image The image to be compressed
     * @return the compressed image
     */
    public CompressedImage compress(BufferedImage image) {
        Raster raster = image.getRaster();
        String[][] binaryValues = new String[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int pixel = raster.getSample(i, j, 0);
                String binaryString = Integer.toBinaryString(pixel);
                binaryValues[i][j] = String.format("%1$8s", binaryString).replace(' ', '0');
            }
        }

        StringBuilder builder = new StringBuilder("");
        for (int plane = 0; plane < 8; plane++) {
            for (int i = 0; i < image.getWidth(); i++) {
                int count = 0;
                char character = '1';
                for (int j = 0; j < image.getHeight(); j++) {
                    if (binaryValues[i][j].charAt(plane) == character) {
                        count++;
                    } else {
                        builder.append(character).append(BIT_SEPERATOR).append(count).append(CHARACTER_SEPERATOR);
                        count = 1;
                        character = binaryValues[i][j].charAt(plane);
                    }
                }
                // Append what is left to the string
                builder.append(character).append(BIT_SEPERATOR).append(count).append(CHARACTER_SEPERATOR);
                builder.append(ROW_SEPERATOR);
            }
        }
        CompressedImage compressedImage = new CompressedImage(image.getWidth(), image.getHeight(), builder.toString());
        return compressedImage;
    }

    /**
     * Decompresses an image. Bit planes will be converted back to a buffered image.
     *
     * @param compressedImage Image to be Decompressed
     * @return The decompressed image as a {@link BufferedImage}
     */
    public BufferedImage decompress(CompressedImage compressedImage) {
        String[] rows = compressedImage.getBinaryRepresentation().split(ROW_SEPERATOR);
        StringBuilder[][] builders = new StringBuilder[compressedImage.getWidth()][compressedImage.getHeight()];
        int rowNumber = 0;
        for (int plane = 0; plane < 8; plane++) {
            for (int i = 0; i < compressedImage.getWidth(); i++) {

                // Split each rowNumber using Character separator
                String[] parts = rows[rowNumber].split(CHARACTER_SEPERATOR);
                StringBuilder rowBuilder = new StringBuilder("");
                for (String part : parts) {
                    char character = part.split(BIT_SEPERATOR)[0].charAt(0);
                    int count = Integer.parseInt(part.split(BIT_SEPERATOR)[1]);
                    for (int x = 0; x < count; x++) {
                        rowBuilder.append(character);
                    }
                }

                for (int j = 0; j < compressedImage.getHeight(); j++) {
                    if (builders[i][j] == null) builders[i][j] = new StringBuilder("");
                    builders[i][j].append(rowBuilder.charAt(j));
                }
                rowNumber++;
            }
        }

        BufferedImage bufferedImage = new BufferedImage(compressedImage.getWidth()
                , compressedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < compressedImage.getWidth(); i++) {
            for (int j = 0; j < compressedImage.getHeight(); j++) {
                int pixelVal = Integer.parseInt(builders[i][j].toString(), 2);
                bufferedImage.setRGB(i, j, new Color(pixelVal, pixelVal, pixelVal).getRGB());
            }
        }
        return bufferedImage;
    }
}
