package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.ImageResponse;
import com.app.linkedinclone.model.enums.ImageType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    @Transactional
    ResponseEntity<ImageResponse> uploadImage(String email, MultipartFile imageUri, ImageType imageType);

    @Transactional
    ResponseEntity<ImageResponse> deleteImage(String email, MultipartFile imageUri);

    @Transactional
    String getProfileImageUrl(String email);

    @Transactional
    void initAllUserImages();

    boolean initUserDefaultImage(User user);
}
