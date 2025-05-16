package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Image;
import com.app.linkedinclone.model.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByName(String name);
    Image findByImageUri(String imageUri);
    Image findByUserIdAndTypeIs(Long userId, ImageType type);
}
