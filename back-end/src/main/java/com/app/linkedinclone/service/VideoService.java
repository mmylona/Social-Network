package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dto.VideoResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface VideoService {
    @Transactional
    ResponseEntity<VideoResponse> uploadVideo(String email, MultipartFile videoFile);
    @Transactional
    ResponseEntity<VideoResponse> deleteVideo(String email, Long videoId);
    @Transactional
    String getVideoUrl(Long videoId);
}