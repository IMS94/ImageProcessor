package com.imesha.imageprocessor.core;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 2016
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class ImageConverter {

    /**
     * Converts a given BufferedImage to gray scale BufferedImage.
     *
     * @param image the image to be converted to gray
     * @return
     */
    public static BufferedImage converToGrayScale(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                double r = color.getRed() * 0.3;
                double g = color.getGreen() * 0.59;
                double b = color.getBlue() * 0.11;

                int avg = (int) (r + g + b);
                Color newColor = new Color(avg, avg, avg);
                newImage.setRGB(i, j, newColor.getRGB());
            }
        }
        return newImage;
    }
}
