package com.example.image.controller;

import com.example.image.dto.TransformRequestDto;
import com.example.image.security.JwtUtils;
import com.example.image.service.FileStorageService;
import com.example.image.service.ImageProcService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;


@RestController
public class ImageUploadController {

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ImageProcService imageProcService;


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


    @PostMapping("/images/transform/{uuidImage}")
    public ResponseEntity<?> getTransformImage(@PathVariable String uuidImage,
                                               @RequestBody TransformRequestDto transformRequest)
            throws IOException, Exception{
        String transformFilePath = imageProcService.transformImageHandler(uuidImage, transformRequest);
        return ResponseEntity.ok().body(transformFilePath);
    }
}
