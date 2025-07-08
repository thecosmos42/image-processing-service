package com.example.image.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
@Slf4j
public class ImageValidationUtils {

    @Value("${image.supported-formats}")
    private String SUPPORTED_FILE_TYPES;

    public void validate(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!hasValidExtension(file.getOriginalFilename())) {
            throw new IllegalArgumentException("Invalid file extension. Allowed: "+SUPPORTED_FILE_TYPES );
        }

        if (!isImage(file)) {
            throw new IllegalArgumentException("Uploaded file is not a valid image");
        }
    }

    private boolean hasValidExtension(String filename) {
        String[] fileTypes = SUPPORTED_FILE_TYPES.split(",");
        for (String fileType : fileTypes) {
            log.info(fileType);
            if (filename.toLowerCase().endsWith(fileType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        return image != null;
    }
}
