package com.example.image.utils;

import com.example.image.dto.TransformRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Utility class providing image transformation operations
 * such as crop, rotate, resize, filter, and format conversion.
 *
 * Each method reads the image from the file system, applies the transformation,
 * and writes the result back to the same or a new file.
 */
@Slf4j
@Component
public class TransformUtils {

    /**
     * Crops the image based on the coordinates and size provided in the transform request.
     *
     * @param transformFilePath Path to the image file
     * @param transformRequestDto DTO containing crop configuration
     * @throws IOException if file read/write fails
     * @throws IllegalArgumentException if crop size exceeds image bounds
     */
    public void cropImage(String transformFilePath, TransformRequestDto transformRequestDto)
            throws IOException, IllegalArgumentException {

        BufferedImage originalImage = ImageIO.read(new File(transformFilePath));
        int x = transformRequestDto.getCrop().getX();
        int y = transformRequestDto.getCrop().getY();
        int width = transformRequestDto.getCrop().getWidth();
        int height = transformRequestDto.getCrop().getHeight();

        if(width > originalImage.getWidth() || height > originalImage.getHeight()){
            throw new IllegalArgumentException("Height or width must be within bounds");
        }

        // Validate that crop rectangle stays within the image
        if ((x + width) > originalImage.getWidth() || (y + height) > originalImage.getHeight()) {
            throw new IllegalArgumentException("Crop rectangle exceeds image boundaries");
        }

        BufferedImage cropImage = originalImage.getSubimage(x, y, width, height);
        ImageIO.write(cropImage,"jpg", new File(transformFilePath));
    }

    /**
     * Rotates the image by a specified number of degrees.
     *
     * @param transformFilePath Path to the image file
     * @param transformRequestDto DTO containing rotation angle
     * @throws IOException if file operations fail
     */
    public void rotateImage(String transformFilePath, TransformRequestDto transformRequestDto)
            throws IOException {
        log.info("Rotating image by {}", transformRequestDto.getRotate());
        BufferedImage originalImage = ImageIO.read(new File(transformFilePath));
        BufferedImage rotateImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        Graphics2D g2 = rotateImage.createGraphics();
        g2.rotate(Math.toRadians(transformRequestDto.getRotate()), originalImage.getWidth()/2,
                originalImage.getHeight() /2);
        g2.drawImage(originalImage, null, 0, 0);

        ImageIO.write(rotateImage,"jpg", new File(transformFilePath));
    }

    /**
     * Resizes the image to the specified width and height.
     *
     * @param transformFilePath Path to the image file
     * @param transformRequestDto DTO containing new dimensions
     * @throws IOException if file operations fail
     */
    public void resizeImage(String transformFilePath, TransformRequestDto transformRequestDto)
            throws IOException{

        log.info("Resizing image to {}, {}", transformRequestDto.getResize().getWidth(),transformRequestDto.getResize().getHeight());
        BufferedImage originalImage = ImageIO.read(new File(transformFilePath));
        BufferedImage resizeImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        Graphics2D g2 = resizeImage.createGraphics();
        g2.drawImage(originalImage.getScaledInstance(
                transformRequestDto.getResize().getWidth(),
                transformRequestDto.getResize().getHeight(),
                Image.SCALE_SMOOTH), 0, 0, null);
        g2.dispose();
        ImageIO.write(resizeImage,"jpg", new File(transformFilePath));
    }

    /**
     * Converts the image format (e.g., jpg to png) and updates the file path.
     *
     * @param transformFilePath Original file path
     * @param transformRequestDto DTO containing target format
     * @return New file path with updated format
     * @throws IOException if file operations fail
     */
    public String formatImage(String transformFilePath, TransformRequestDto transformRequestDto)
            throws IOException{

        log.info("Converting image to {}", transformRequestDto.getFormat());
        BufferedImage originalImage = ImageIO.read(new File(transformFilePath));
        String[] splitPath = transformFilePath.split("\\.(?=[^\\.]+$)");
        transformFilePath = splitPath[0] + "." + transformRequestDto.getFormat() ;
        ImageIO.write(originalImage, transformRequestDto.getFormat(), new File(transformFilePath));
        return transformFilePath;
    }

    /**
     * Applies grayscale or sepia filter to the image based on request.
     *
     * @param transformFilePath Path to the image file
     * @param transformRequestDto DTO containing filter settings
     * @throws IOException if file operations fail
     */
    public void filterImage(String transformFilePath, TransformRequestDto transformRequestDto)
            throws IOException{

        BufferedImage originalImage = ImageIO.read(new File(transformFilePath));
        BufferedImage filterImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        if(transformRequestDto.getFilters().getGrayscale()!=Boolean.FALSE){
            log.info("Applying greyscale filter to image");
            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {
                    int rgb = originalImage.getRGB(x, y);

                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;

                    int gray = (r + g + b) / 3;

                    int newPixel = (gray << 16) | (gray << 8) | gray;
                    filterImage.setRGB(x, y, newPixel);
                }
            }
            ImageIO.write(filterImage, "jpg", new File(transformFilePath));
        } else if(transformRequestDto.getFilters().getSepia()!=Boolean.FALSE) {
            log.info("Applying Sepia filter to image");
            for (int y = 0; y < originalImage.getHeight(); y++) {
                for (int x = 0; x < originalImage.getWidth(); x++) {
                    int rgb = originalImage.getRGB(x, y);

                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;

                    int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                    int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                    int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                    // Clamp color values to 255
                    tr = Math.min(255, tr);
                    tg = Math.min(255, tg);
                    tb = Math.min(255, tb);

                    int newPixel = (tr << 16) | (tg << 8) | tb;
                    filterImage.setRGB(x, y, newPixel);
                }
            }
            ImageIO.write(filterImage, "jpg", new File(transformFilePath));
        }

    }
}
