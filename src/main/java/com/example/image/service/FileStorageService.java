package com.example.image.service;

import com.example.image.model.ImageMetadata;
import com.example.image.repository.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class FileStorageService {

    @Value("${image.input.path}")
    String FILE_DIR;

    @Autowired
    private ImageMetadataRepository imageMetadataRepository;


    public String saveFile(MultipartFile file, String userName) throws IOException{
        if (file.isEmpty()){
            throw new IllegalArgumentException("File is empty");
        }

        UUID uuidImage = UUID.randomUUID();

        // Create the uploads directory if it doesn't exist
        // Input location is FILE_DIR + USERNAME + UUID
        Path path = Paths.get(FILE_DIR, userName, uuidImage.toString());
        File uploadDir = new File(path.toUri());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Build path of file
        Path filePath = Paths.get(String.valueOf(path), file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        //Save Image metadata to db
        ImageMetadata newImage = ImageMetadata.builder()
                .id(uuidImage)
                .imageName(file.getOriginalFilename())
                .storagePath(filePath.toString())
                .userName(userName)
                .build();

        imageMetadataRepository.save(newImage);

        return uuidImage.toString();
    }

    public Resource getFile(String filename) throws MalformedURLException {
        Path filePath = Paths.get(FILE_DIR, filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new IllegalArgumentException("File is not present");
        }

        return resource;
    }

}
