package com.example.image.controller;

import com.example.image.dto.TransformRequestDto;
import com.example.image.security.JwtUtils;
import com.example.image.service.FileStorageService;
import com.example.image.service.ImageProcService;
import com.example.image.utils.ImageValidationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@Import(ImageUploadControllerTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ImageProcService imageProcService;

    @Autowired
    private ImageValidationUtils imageValidationUtils;

    @Test
    void testUploadImage_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());

        when(jwtUtils.getUserNameFromToken("valid-token")).thenReturn("testUser");
        when(fileStorageService.saveFile(any(), eq("testUser"))).thenReturn("1234");

        mockMvc.perform(multipart("/images/upload")
                        .file(file)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("File has been saved with ImageID: 1234"));
    }

    @Test
    void testTransformImage_success() throws Exception {
        TransformRequestDto request = new TransformRequestDto(); // Populate fields as needed
        when(imageProcService.transformImageHandler(eq("imageId123"), any())).thenReturn("some/path/image_transform.jpg");

        mockMvc.perform(post("/images/transform/imageId123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // You can replace with actual JSON
                .andExpect(status().isOk())
                .andExpect(content().string("some/path/image_transform.jpg"));
    }
}
