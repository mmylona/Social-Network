package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dao.Video;
import com.app.linkedinclone.model.dto.VideoResponse;
import com.app.linkedinclone.repository.UserRepository;
import com.app.linkedinclone.repository.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<VideoResponse> uploadVideo(String email, MultipartFile videoFile) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Video video = new Video();
        video.setName(videoFile.getOriginalFilename());
        video.setUserId(user.getId());

        try {
            video.setContent(videoFile.getBytes());
            String videoUrl = "data:video/mp4;base64," + Base64.getEncoder().encodeToString(videoFile.getBytes());
            video.setVideoUrl(videoUrl);

            videoRepository.save(video);
            Long videoId = videoRepository.findByUserId(user.getId()).getId();
            return ResponseEntity.ok(new VideoResponse("Video uploaded successfully", videoUrl,videoId));
        } catch (Exception e) {
            log.error("Error while uploading video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new VideoResponse("Video upload failed", null,null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<VideoResponse> deleteVideo(String email, Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        try {
            videoRepository.delete(video);
            return ResponseEntity.ok(new VideoResponse("Video deleted successfully", null,null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new VideoResponse("Video deletion failed", null,null));
        }
    }

    @Override
    @Transactional
    public String getVideoUrl(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));
        return video.getVideoUrl();
    }
}