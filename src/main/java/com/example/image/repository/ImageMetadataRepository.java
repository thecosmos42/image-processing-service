package com.example.image.repository;

import com.example.image.model.ImageMetadata;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, UUID> {

    Page<ImageMetadata> findAllByUserNameOrderByCreatedTsp(String userName, Pageable page);
}