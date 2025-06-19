package com.example.image.repository;

import com.example.image.model.ImageMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, UUID> {

}