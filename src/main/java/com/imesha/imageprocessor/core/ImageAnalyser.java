package com.imesha.imageprocessor.core;

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
public class ImageAnalyser {

    public static int calculateAverageDistortion(BufferedImage originalImage, BufferedImage modifiedImage)
            throws Exception {
        if (originalImage.getHeight() != modifiedImage.getHeight() ||
                originalImage.getWidth() != modifiedImage.getWidth())
            throw new Exception("Images cannot be compared");
        int distortion = 0;
        Raster originalImageRaster = originalImage.getRaster();
        Raster modifiedImageRaster = modifiedImage.getRaster();
        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                distortion += Math
                        .abs(originalImageRaster.getSample(i, j, 0) - modifiedImageRaster.getSample(i, j, 0));
            }
        }
        distortion = (int) ((float) distortion) / (originalImage.getHeight() * originalImage.getWidth());
        return distortion;
    }

    public static double calculateStandardDeviation(BufferedImage originalImage, BufferedImage modifiedImage)
            throws Exception {
        if (originalImage.getHeight() != modifiedImage.getHeight() ||
                originalImage.getWidth() != modifiedImage.getWidth())
            throw new Exception("Images cannot be compared");
        int differenceSum = 0;
        Raster originalImageRaster = originalImage.getRaster();
        Raster modifiedImageRaster = modifiedImage.getRaster();
        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                int difference = originalImageRaster.getSample(i, j, 0) ^ 2 - modifiedImageRaster.getSample(i, j, 0) ^ 2;
                differenceSum += Math.abs(difference ^ 2);
            }
        }
        double standardDeviation = Math.sqrt(differenceSum / (originalImage.getHeight() * originalImage.getWidth()));
        return standardDeviation;
    }
}