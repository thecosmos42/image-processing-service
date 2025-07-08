package com.example.image.controller;

import com.example.image.repository.ImageMetadataRepository;
import com.example.image.security.JwtUtils;
import com.example.image.service.FileStorageService;
import com.example.image.service.ImageProcService;
import com.example.image.utils.ImageValidationUtils;
import com.example.image.utils.TransformUtils;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ImageUploadControllerTestConfig {

    @Bean
    public FileStorageService fileStorageService() {
        return Mockito.mock(FileStorageService.class);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return Mockito.mock(JwtUtils.class);
    }

    @Bean
    public ImageProcService imageProcService() {
        return Mockito.mock(ImageProcService.class);
    }

    @Bean
    public ImageMetadataRepository imageMetadataRepository() {
        return Mockito.mock(ImageMetadataRepository.class);
    }

    @Bean
    public TransformUtils transformUtils() {
        return Mockito.mock(TransformUtils.class);
    }

    @Bean
    public ImageValidationUtils imageValidationUtils() {
        return Mockito.mock(ImageValidationUtils.class);
    }
}
