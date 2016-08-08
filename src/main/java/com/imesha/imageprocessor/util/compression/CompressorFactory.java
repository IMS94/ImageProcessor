package com.imesha.imageprocessor.util.compression;

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 imesha
 */
public class CompressorFactory {
    public static final int ENTROPHY_CODING = 1;
    public static final int RUN_LENGTH_CODING = 2;

    private static CompressorFactory instance;

    private CompressorFactory() {
    }

    /**
     * Static method to get the Factory instance.
     *
     * @return CompressorFactory instance
     */
    public static CompressorFactory getInstance() {
        if (instance == null) instance = new CompressorFactory();
        return instance;
    }

    /**
     * Factory method to get the ImageCompressor instance.
     *
     * @param type The type of the image compressor required. Types are defined as static final int.
     * @return The {@link ImageCompressor} instance
     */
    public ImageCompressor getCompressor(int type) {
        ImageCompressor compressor = null;
        switch (type) {
            case ENTROPHY_CODING:
                compressor = new HuffmanCodingCompressor();
            case RUN_LENGTH_CODING:
                compressor = new BitPlaneCodingCompressor();
        }
        return compressor;
    }

}
