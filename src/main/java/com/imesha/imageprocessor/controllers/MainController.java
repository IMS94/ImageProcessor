package com.imesha.imageprocessor.controllers;

import com.imesha.imageprocessor.core.ImageAnalyser;
import com.imesha.imageprocessor.core.ImageConverter;
import com.imesha.imageprocessor.core.ImageSampler;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    private Button convertToGrayButton;
    @FXML
    private Button downSampleButton;
    @FXML
    private Button upSampleButton;

    @FXML
    private Label messageLabel;

    private BufferedImage bufferedImage;
    private BufferedImage originalGrayImage;

    public void initialize(URL location, ResourceBundle resources) {
        setOpenMenuItemActionListener();
        setConvertToGrayButtonActionListener();
        setDownSampleButtonActionListener();
        setUpSampleButtonActionListener();
        setupUI();
    }

    /**
     * Initially hide all the buttons.
     */
    private void setupUI() {
        downSampleButton.setVisible(false);
        upSampleButton.setVisible(false);
        convertToGrayButton.setVisible(false);
        messageLabel.setText("");
    }

    private void setOpenMenuItemActionListener() {
        // Open an image, then show the image in the imageView
        openMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png");
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setTitle("Select an Image to be opened");
                File file = fileChooser.showOpenDialog(parentPane.getScene().getWindow());
                try {
                    bufferedImage = ImageIO.read(file);
                    setupUI();
                    convertToGrayButton.setVisible(true);
                    MainController.showImageInUI(bufferedImage, imageView);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private void setConvertToGrayButtonActionListener() {
        this.convertToGrayButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (bufferedImage != null) {
                    BufferedImage convertedImage = ImageConverter.converToGrayScale(bufferedImage);
                    bufferedImage = convertedImage;
                    // Store the gray image also in the originalGrayImage variable
                    originalGrayImage = convertedImage;

                    MainController.showImageInUI(convertedImage, imageView);
                    convertToGrayButton.setVisible(false);
                    downSampleButton.setVisible(true);
                }
            }
        });
    }


    /**
     * Sets the listener for the down sample action.
     * The listener will call the ImageSampler to down sample the image.
     * In the meantime, progress indicator will be updated to show the progress of sampling process.
     */
    private void setDownSampleButtonActionListener() {
        this.downSampleButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (bufferedImage != null) {
                    // Down Sample the image and then, Update the UI later. Once the down sampled image is returned,
                    // It will be displayed using the UI thread.
                    final Thread samplingThread = new Thread() {
                        public void run() {
                            try {
                                final BufferedImage sampledImage = ImageSampler.downSample(bufferedImage);
                                bufferedImage = sampledImage;
                                downSampleButton.setVisible(false);
                                upSampleButton.setVisible(true);
                                MainController.showImageInUI(sampledImage, previousImageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    samplingThread.start();
                }
            }
        });
    }


    /**
     * Sets the listener to listen for action events on up sample button.
     * This will call the utility class @{@link ImageSampler} to up sample the image.
     */
    private void setUpSampleButtonActionListener() {
        this.upSampleButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (bufferedImage != null) {
                    // Up Sample the image and then, Update the UI later. Once the up sampled image is returned,
                    // It will be displayed using the UI thread.
                    final Thread samplingThread = new Thread() {
                        public void run() {
                            try {
                                final BufferedImage sampledImage = ImageSampler.upSample(bufferedImage);
                                bufferedImage = sampledImage;
                                MainController.showImageInUI(sampledImage, additionalImageView);
                                upSampleButton.setVisible(false);
                                calculateAndShowDistortion();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    samplingThread.start();
                }
            }
        });
    }


    /**
     * Calculates the distortion between the original gray image and the final up sampled image
     */
    private void calculateAndShowDistortion() {
        try {
            int distortion = ImageAnalyser.calculateAverageDistortion(this.originalGrayImage, this.bufferedImage);
            System.out.println(distortion);
            double sd = ImageAnalyser.calculateStandardDeviation(this.originalGrayImage, this.bufferedImage);
            MainController.showMessage("Distortion : \n\t" + distortion + "\nSD : \n\t" + sd, messageLabel);
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