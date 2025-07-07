package com.example.image.service;

import com.example.image.repository.ImageMetadataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    @Test
    public void testGetFile_ImageNotFound_ThrowsIOException() {
        String imageId = UUID.randomUUID().toString();
        when(imageMetadataRepository.findById(UUID.fromString(imageId))).thenReturn(Optional.empty());

        assertThrows(IOException.class, () -> {
            fileStorageService.getFile(imageId, "input");
        });
    }
}

