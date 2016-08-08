package com.imesha.imageprocessor.util.compression;

/*
 * @author imesha
 * @date 8/5/16.
 */

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.Map;

public class HuffmanCodingCompressor implements ImageCompressor {

    public CompressedImage compress(BufferedImage originalImage) {
        Map<Integer, Integer> histogram = getHistogram(originalImage);

        return null;
    }

    private Map<Integer, Integer> getHistogram(BufferedImage image) {
        Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
        Raster raster = image.getRaster();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int count = histogram.containsKey(raster.getSample(i, j, 0)) ?
                        histogram.get(raster.getSample(i, j, 0)) : 0;
                histogram.put(raster.getSample(i, j, 0), count + 1);
            }
        }
        return histogram;
    }

    public BufferedImage decompress(CompressedImage compressedImage) {
        return null;
    }
}
