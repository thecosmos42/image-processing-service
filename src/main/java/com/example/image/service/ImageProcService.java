package com.example.image.service;

import com.example.image.dto.TransformRequestDto;
import com.example.image.model.ImageMetadata;
import com.example.image.repository.ImageMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.image.utils.TransformUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


/**
 * Service class that handles image transformation operations
 * such as resize, crop, rotate, filter, and format changes.
 *
 * It reads the original image, applies transformations based on user request,
 * and stores the path to the transformed image in the database.
 */
@Service
@Slf4j
public class ImageProcService {


    @Autowired
    private ImageMetadataRepository imageMetadataRepository;

    @Autowired
    private TransformUtils transformUtils;


    /**
     * Handles transformation requests for an image.
     *
     * @param uuidImage        the UUID string of the image to be transformed
     * @param transformRequest the transformation instructions including resize, crop, rotate, etc.
     * @return the file path to the transformed image
     * @throws IOException              if the image is not found or an IO error occurs
     * @throws IllegalArgumentException if the image metadata is not found
     */
    public String transformImageHandler(String uuidImage, TransformRequestDto transformRequest)
            throws IOException, Exception {

        Optional<ImageMetadata> imageMeta = imageMetadataRepository.findById(UUID.fromString(uuidImage));
        if(imageMeta.isEmpty()){
            throw new IOException("Image ID is invalid");
        }
        String pathName = imageMeta.get().getInputPath();
        File file = new File(pathName);
        if(!file.exists()){
            throw new IOException("File path does not exist");
        }

        // Create a copy of the original image and add the suffix '_transform' to filename
        String[] splitPath = pathName.split("\\.(?=[^\\.]+$)");
        BufferedImage originalImage = ImageIO.read(new File(pathName));
        String transformFilePath = splitPath[0] + "_transform." +  splitPath[1];
        ImageIO.write(originalImage, splitPath[1], new File(transformFilePath));

        if (transformRequest.getResize() != null){
            log.info("Resizing image");
            transformUtils.resizeImage(transformFilePath, transformRequest);
        }
        if(transformRequest.getCrop() != null){
            log.info("Cropping image");
            transformUtils.cropImage(transformFilePath, transformRequest);
        }
        if (transformRequest.getRotate() != null){
            log.info("Rotating image");
            transformUtils.rotateImage(transformFilePath, transformRequest);
        }
        if (transformRequest.getFilters() != null){
            log.info("Applying filters to image");
            transformUtils.filterImage(transformFilePath, transformRequest);
        }
        if (transformRequest.getFormat() != null){
            log.info("Format image type");
            transformFilePath = transformUtils.formatImage(transformFilePath, transformRequest);
        }

        Optional<ImageMetadata> imgMeta = imageMetadataRepository.findById(UUID.fromString(uuidImage));
        if(imgMeta.isEmpty()){
            throw new IllegalArgumentException("Image UUID doesn't exist");
        }

        //Save Image metadata to db
        log.info("Saving transformed filepath {} to database", transformFilePath);
        ImageMetadata imgMetaData = imgMeta.get();
        imgMetaData.setTransformPath(transformFilePath);
        imageMetadataRepository.save(imgMetaData);

        return transformFilePath;

    }
}
