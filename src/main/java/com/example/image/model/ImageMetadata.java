package com.example.image.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    private String userName;

    private String imageName;

    private String inputPath;

    private String transformPath;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedTsp;
}
