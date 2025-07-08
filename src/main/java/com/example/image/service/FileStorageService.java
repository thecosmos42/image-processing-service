package com.example.image.service;

import com.example.image.model.ImageMetadata;
import com.example.image.repository.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;


@Service
public class FileStorageService {

    @Value("${image.input.path}")
    String FILE_DIR;

    @Autowired
    private ImageMetadataRepository imageMetadataRepository;


    /**
     * Saves the uploaded image to disk and metadata to the database.
     *
     * @param file     Multipart image file
     * @param userName Username associated with the image
     * @return UUID string of the stored image
     * @throws IOException if file saving fails
     */
    public String saveFile(MultipartFile file, String userName) throws IOException{
        if (file.isEmpty()){
            throw new IllegalArgumentException("File is empty");
        }

        UUID uuidImage = UUID.randomUUID();

        // Create a directory: FILE_DIR/userName/uuid/
        Path path = Paths.get(FILE_DIR, userName, uuidImage.toString());
        File uploadDir = new File(path.toUri());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Build path of file
        Path filePath = Paths.get(String.valueOf(path), file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        ImageMetadata newImage = ImageMetadata.builder()
                .id(uuidImage)
                .imageName(file.getOriginalFilename())
                .inputPath(filePath.toString())
                .userName(userName)
                .build();

        imageMetadataRepository.save(newImage);

        return uuidImage.toString();
    }

    /**
     * Retrieves the image as a Spring Resource.
     *
     * @param imageId UUID of the image
     * @param type    "input" or "transform" image type
     * @return image as a Resource
     * @throws IOException if image not found
     */
    public Resource getFile(String imageId, String type) throws IOException {
        Optional<ImageMetadata> imgMeta = imageMetadataRepository.findById(UUID.fromString(imageId));
        if(imgMeta.isEmpty()){
            throw new IOException("Image ID doesn't exist");
        }
        Path filePath = null;
        if(type.equals("input")){
            filePath = Paths.get(imgMeta.get().getInputPath());
        } else if(type.equals("transform")){
            filePath = Paths.get(imgMeta.get().getTransformPath());
            if (filePath.toString().isEmpty()) {
                throw new IOException("Transformed image not available");
            }
        }
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new IOException("File is not present");
        }

        return resource;
    }


    /**
     * Retrieves all the image metadata based on user
     *
     * @param userName username
     * @param page    page parameters
     * @return paginated response of ImageMetadata
     * @throws IOException if image not found
     */
    public Page<ImageMetadata> getAllImageMetadataByUser(String userName, Pageable page) throws IOException {
        Page<ImageMetadata> imgData = imageMetadataRepository.findAllByUserNameOrderByCreatedTsp(userName, page);
        if(imgData.isEmpty()){
            throw new IOException("User has no images uploaded");
        }
        return imgData;
    }

}
