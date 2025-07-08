package com.example.image.controller;

import com.example.image.dto.TransformRequestDto;
import com.example.image.model.ImageMetadata;
import com.example.image.security.JwtUtils;
import com.example.image.service.FileStorageService;
import com.example.image.service.ImageProcService;
import org.springframework.data.domain.Page;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Controller for handling image upload, transformation, and retrieval operations.
 * All endpoints require a valid JWT token in the Authorization header.
 */
@RestController
public class ImageController {

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ImageProcService imageProcService;


    /**
     * Uploads an image file for the authenticated user.
     *
     * @param file       the image file to upload
     * @param authHeader the Authorization header containing the JWT token
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/images/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestHeader("Authorization") String authHeader) {
        try {

            String token = authHeader.substring(7);
            String userName = jwtUtils.getUserNameFromToken(token);

            String filePath = storageService.saveFile(file, userName);
            return ResponseEntity.ok().body("File has been saved with ImageID: " + filePath);
        } catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file to local");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty/File size exceeded limit");
        }
    }

    /**
     * Downloads a transformed or original image file based on type.
     *
     * @param imageId the ID of the image
     * @param type    the type of image (e.g., "input" or "transform")
     * @return the file as a downloadable resource or an error response
     */
    @GetMapping("/images/download/{type}/{imageId}")
    public ResponseEntity<?> getTransformImage(@PathVariable String imageId, @PathVariable String type) throws IOException{
        try{
            Resource image = storageService.getFile(imageId, type);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename(image.getFilename())
                                    .build()
                                    .toString())
                    .body(image);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        } catch(MalformedURLException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid filename or file is empty");
        }
    }


    /**
     * Applies a transformation to the specified image.
     *
     * @param uuidImage        the UUID of the image to transform
     * @param transformRequest the transformation request DTO containing settings
     * @return path to the transformed image file
     */
    @PostMapping("/images/transform/{uuidImage}")
    public ResponseEntity<?> getTransformImage(@PathVariable String uuidImage,
                                               @RequestBody TransformRequestDto transformRequest)
            throws IOException, Exception{
        String transformFilePath = imageProcService.transformImageHandler(uuidImage, transformRequest);
        return ResponseEntity.ok().body(transformFilePath);
    }

    /**
     * Retrieves paginated metadata for all images uploaded by the authenticated user.
     *
     * @param page       the page number (default is 0)
     * @param size       the number of items per page (default is 10)
     * @param authHeader the Authorization header containing the JWT token
     * @return paginated list of image metadata
     */
    @GetMapping("/images")
    public ResponseEntity<Page<ImageMetadata>> getAllImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) throws IOException {

        Pageable pageable = PageRequest.of(page, size);
        String token = authHeader.substring(7);
        String userName = jwtUtils.getUserNameFromToken(token);
        return ResponseEntity.ok().body(storageService.getAllImageMetadataByUser(userName, pageable));
    }
}
