package com.app.linkedinclone.service;

import com.app.linkedinclone.exception.ImageUploadingException;
import com.app.linkedinclone.model.dao.Image;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.ImageResponse;
import com.app.linkedinclone.model.enums.ImageType;
import com.app.linkedinclone.model.enums.ResponseStatus;
import com.app.linkedinclone.repository.ImageRepository;
import com.app.linkedinclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {
    private static final String IMAGE_NOT_FOUND = "Image not found";
    private static final String IMAGE_UPLOAD_FAILED = "Image upload failed";
    private static final String IMAGE_DELETION_FAILED = "Image deletion failed";
    private static final String IMAGE_DELETED_SUCCESSFULLY = "Image deleted successfully";
    private static final String IMAGE_UPLOAD_SUCCESS = "Image uploaded successfully";
    private static final String IMAGE_DATA_TYPE = "data:image/jpeg;base64,";
    private static final String DEFAULT_IMAGE = "user-default-icon.jpg";
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<ImageResponse> uploadImage(String email, MultipartFile imageUri, ImageType imageType) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Image image = new Image();
        if(imageType.equals(ImageType.PROFILE)){
            Image imageProfile = imageRepository.findByUserIdAndTypeIs(user.getId(), ImageType.PROFILE);
            if(!isNull(imageProfile)){
                log.info("Deleting previous profile image : {}", imageProfile.getName());
                imageRepository.delete(imageProfile);
            }
        }
        image.setName(imageUri.getOriginalFilename());
        image.setUserId(user.getId());
        image.setType(imageType);


        try {
            log.info("Uploading image : {}", imageUri.getOriginalFilename());
            image.setPicByte(imageUri.getBytes());
            String imageUriString = IMAGE_DATA_TYPE + Base64.getEncoder().encodeToString(imageUri.getBytes());
            image.setImageUri(imageUriString);

            Image imageSaved = imageRepository.save(image);
            return ResponseEntity.ok().body(ImageResponse.builder()
                    .message(IMAGE_UPLOAD_SUCCESS).status(ResponseStatus.SUCCESS).imageUri(imageUriString).imageId(imageSaved.getId()).build());
        } catch (Exception e) {
            log.error("Error while uploading image : {}", e.getMessage());
            return ResponseEntity.ok().body(ImageResponse.builder()
                    .message(IMAGE_UPLOAD_FAILED).status(ResponseStatus.ERROR).build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ImageResponse> deleteImage(String email, MultipartFile imageUri) {
        Image image = imageRepository.findByImageUri(imageUri.getOriginalFilename());

        if (isNull(image))
            return ResponseEntity.ok().body(ImageResponse.builder()
                    .message(IMAGE_NOT_FOUND).status(ResponseStatus.ERROR).build());


        try {
            imageRepository.delete(image);
            return ResponseEntity.ok().body(ImageResponse.builder()
                    .message(IMAGE_DELETED_SUCCESSFULLY).status(ResponseStatus.SUCCESS).build());
        } catch (Exception e) {
            return ResponseEntity.ok().body(ImageResponse.builder()
                    .message(IMAGE_DELETION_FAILED).status(ResponseStatus.ERROR).build());
        }
    }

    @Override
    public String getProfileImageUrl(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Image image = imageRepository.findByUserIdAndTypeIs(user.getId(), ImageType.PROFILE);
        if (isNull(image))
            return null;

        return image.getImageUri();
    }

    @Override
    public void initAllUserImages() {

        userRepository.findAll().forEach(user -> {
            Image image = imageRepository.findByUserIdAndTypeIs(user.getId(), ImageType.PROFILE);
            if (isNull(image)) {
                image = new Image();
                image.setName(DEFAULT_IMAGE);
                image.setUserId(user.getId());
                try {
                    image.setImageUri(IMAGE_DATA_TYPE + Base64.getEncoder().encodeToString(requireNonNull(getClass().getClassLoader().getResourceAsStream(DEFAULT_IMAGE)).readAllBytes()));
                } catch (IOException e) {
                    throw new ImageUploadingException(e.getMessage());
                }
                imageRepository.save(image);
            }
        });
        log.info("All user images initialized");
    }

    @Override
    public boolean initUserDefaultImage(User user) {

        Image image = imageRepository.findByUserIdAndTypeIs(user.getId(), ImageType.PROFILE);
        if (image == null) {
            image = new Image();
            image.setName(DEFAULT_IMAGE);
            image.setUserId(user.getId());
            image.setType(ImageType.PROFILE);
            try {
                image.setImageUri(IMAGE_DATA_TYPE + Base64.getEncoder().encodeToString(requireNonNull(getClass().getClassLoader().getResourceAsStream(DEFAULT_IMAGE)).readAllBytes()));
            } catch (IOException e) {
                throw new ImageUploadingException(e.getMessage());
            }
            imageRepository.save(image);
            return true;
        }
        return false;
    }
}
