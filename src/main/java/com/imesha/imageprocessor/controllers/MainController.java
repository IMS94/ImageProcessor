package com.imesha.imageprocessor.controllers;

import com.imesha.imageprocessor.util.ImageAnalyser;
import com.imesha.imageprocessor.util.ImageConverter;
import com.imesha.imageprocessor.util.ImageSampler;
import com.imesha.imageprocessor.util.ImageUtils;
import com.imesha.imageprocessor.util.compression.CompressedImage;
import com.imesha.imageprocessor.util.compression.CompressorFactory;
import com.imesha.imageprocessor.util.compression.ImageCompressor;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;

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
public class MainController implements Initializable {
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private Pane parentPane;

    @FXML
    private ImageView imageView;
    @FXML
    private ImageView previousImageView;
    @FXML
    private ImageView additionalImageView;

    @FXML
    private Button processButton;

    @FXML
    private Label messageLabel;

    private BufferedImage bufferedImage;
    private BufferedImage originalGrayImage;

    public void initialize(URL location, ResourceBundle resources) {
        setOpenMenuItemActionListener();
        setProcessButtonActionListener();
    }


    private void setProcessButtonActionListener() {
        this.processButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                new Thread() {
                    public void run() {
                        try {
                            // 1. Convert the image to gray scale and show in UI
                            BufferedImage grayImage = ImageConverter.convertToGrayScale(bufferedImage);
                            bufferedImage = grayImage;
                            originalGrayImage = ImageUtils.deepCopy(grayImage);
                            MainController.showImageInUI(grayImage, imageView);
                            MainController.showMessage("Converted to Gray Scale", messageLabel);
                            System.out.println(bufferedImage.getWidth() + "x" + bufferedImage.getHeight());

                            // 2. The image will be down sampled and showed in the UI.
                            BufferedImage downSampledImage = ImageSampler.downSample(bufferedImage);
                            bufferedImage = downSampledImage;
                            MainController.showImageInUI(downSampledImage, previousImageView);
                            MainController.showMessage("Down sampled", messageLabel);
                            System.out.println(bufferedImage.getWidth() + "x" + bufferedImage.getHeight());

                            // 3. The down sampled image will then be up sampled
                            BufferedImage upSampledImage = ImageSampler.upSample(bufferedImage);
                            bufferedImage = upSampledImage;
                            MainController.showImageInUI(upSampledImage, additionalImageView);
                            MainController.showMessage("Up sampled", messageLabel);
                            System.out.println(bufferedImage.getWidth() + "x" + bufferedImage.getHeight());

                            // 4. Calculate and show the statistics.
                            calculateAndShowDistortion();

                            //Compress Images
                            ImageCompressor compressor = CompressorFactory
                                    .getInstance().getCompressor(CompressorFactory.RUN_LENGTH_CODING);
                            CompressedImage compressedImage = compressor.compress(originalGrayImage);

                            // Write the compressed image to a file.
                            FileUtils.writeStringToFile(new File("/tmp/compress.rlc"),
                                    compressedImage.getBinaryRepresentation());

                            // Decompress the image
                            BufferedImage decompressedImage = compressor.decompress(compressedImage);
                            MainController.showImageInUI(decompressedImage, additionalImageView);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private void setOpenMenuItemActionListener() {
        // Open an image, then show the image in the imageView
        openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Images", "*.*");
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setTitle("Select an Image to be opened");
                File file = fileChooser.showOpenDialog(parentPane.getScene().getWindow());
                if (file == null) return;
                try {
                    bufferedImage = ImageIO.read(file);
                    MainController.showImageInUI(bufferedImage, imageView);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }


    /**
     * Calculates the distortion between the original gray image and the final up sampled image
     */
    private void calculateAndShowDistortion() {
        try {
            double distortion = ImageAnalyser.calculateAverageDistortion(this.originalGrayImage, this.bufferedImage);
            double sd = ImageAnalyser.calculateStandardDeviation(this.originalGrayImage, this.bufferedImage);
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder, Locale.US);
            formatter.format("Distortion : \n\t%.2f\nSD : \n\t%.2f", distortion, sd);
            MainController.showMessage(formatter.toString(), messageLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show a given BufferedImage in the UI.
     *
     * @param bufferedImage The image to be shown in the UI
     * @param imageView     The JavaFX ImageView element in which the BufferedImage to be shown.
     */
    private static void showImageInUI(final BufferedImage bufferedImage, final ImageView imageView) {
        Platform.runLater(new Runnable() {
            public void run() {
                Image loadedImage = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(loadedImage);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setCache(true);
            }
        });
    }


    /**
     * Utility method to show a given message in the message are of the UI.
     *
     * @param message      The message to be displayed
     * @param messageLabel The @{@link Label} object in which the message should be displayed
     */
    private static void showMessage(final String message, final Label messageLabel) {
        Platform.runLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

}