package com.example.image.controller;

import com.example.image.dto.TransformRequestDto;
import com.example.image.security.JwtUtils;
import com.example.image.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/images/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader) {
        try {

            String token = authHeader.substring(7);
            String userName = jwtUtils.getUserNameFromToken(token);

            String filePath = storageService.saveFile(file, userName);
            return ResponseEntity.status(HttpStatus.OK).body("File has been saved with ImageID: " + filePath);
        } catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file to local");
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty/File size exceeded limit");
        }
    }

    @GetMapping("/images/download/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename){
        try{
            Resource image = storageService.getFile(filename);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                    .body(image);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        } catch(MalformedURLException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid filename or file is empty");
        }
    }


    @PostMapping("/images/transform/{uuidImage}")
    public ResponseEntity<?> getTransformImage(@PathVariable String uuidImage, @RequestBody TransformRequestDto transformRequest){

        return ResponseEntity.status(HttpStatus.OK).body("Image Transformed");
    }
}
