package com.imesha.imageprocessor.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 imesha
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
public class ImageSampler {

    /**
     * Down samples the given image. This will use the bi-linear interpolation method to down sample the image.
     *
     * @param bufferedImage The image to be down sampled.
     * @return the image which is down sampled.
     * @throws Exception If the image is not in Gray Scale format.
     */
    public static BufferedImage downSample(BufferedImage bufferedImage) throws Exception {
        if (bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY) throw new Exception("incompatible Image Format");
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2, BufferedImage.TYPE_BYTE_GRAY);
        Raster raster = bufferedImage.getData();
        for (int i = 0; i < bufferedImage.getWidth(); i += 2) {
            for (int j = 0; j < bufferedImage.getHeight(); j += 2) {
                int avgValue = raster.getSample(i, j, 0);
                avgValue += raster.getSample(i, j + 1, 0);
                avgValue += raster.getSample(i + 1, j, 0);
                avgValue += raster.getSample(i + 1, j + 1, 0);

                avgValue = avgValue / 4;
                newImage.setRGB(i / 2, j / 2, new Color(avgValue, avgValue, avgValue).getRGB());
            }
        }
        return newImage;
    }


    /**
     * Up samples an image. This will loop through the existing image and calculate the new pixel value
     * of the new image. This is using the nearest neighbour method to do this.
     *
     * @param bufferedImage The image to be down sampled
     * @return The up sampled image.
     */
    public static BufferedImage upSample(BufferedImage bufferedImage) {
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth() * 2, bufferedImage.getHeight() * 2, BufferedImage.TYPE_BYTE_GRAY);
        Raster raster = bufferedImage.getData();
        for (int i = 0; i < newImage.getWidth(); i++) {
            for (int j = 0; j < newImage.getHeight(); j++) {
                int val = 0;
                int pixelCount = 0;

                val = raster.getSample(i / 2, j / 2, 0);
                pixelCount++;

                if (i / 2 + 1 < bufferedImage.getWidth()) {
                    val += raster.getSample(i / 2 + 1, j / 2, 0);
                    pixelCount++;
                    if (j / 2 + 1 < bufferedImage.getHeight()) {
                        val += raster.getSample(i / 2 + 1, j / 2 + 1, 0);
                        pixelCount++;
                    }
                }

                if (j / 2 + 1 < bufferedImage.getHeight()) {
                    val += raster.getSample(i / 2, j / 2 + 1, 0);
                    pixelCount++;
                }

                /*
                 * Finally, find the pixel value by dividing the Integer val by pixelCount.
                 */
                int pixelValue = (int) ((float) val) / pixelCount;
                newImage.setRGB(i, j, new Color(pixelValue, pixelValue, pixelValue).getRGB());
            }
        }
        return newImage;
    }
}
