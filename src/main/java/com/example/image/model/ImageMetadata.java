package com.example.image.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="image_metadata")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadata {

    @Id
    private UUID id;

    @NotBlank(message = "Username cannot be blank")
    private String userName;

    @NotBlank(message = "Image name cannot be blank")
    private String imageName;

    @NotBlank(message = "Input location cannot be blank")
    private String inputPath;

    private String transformPath;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdTsp;

    @UpdateTimestamp
    private LocalDateTime updatedTsp;
}
