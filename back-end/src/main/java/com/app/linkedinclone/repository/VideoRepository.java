package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Video findByUserId(Long userId);
    Video findByPostId(Long postId);
    Video findByVideoUrl(String videoUrl);
    Video findByUserIdAndPostId(Long userId, Long postId);
    Video findByUserIdAndVideoUrl(Long userId, String videoUrl);
    Video findByPostIdAndVideoUrl(Long postId, String videoUrl);
    Video findByUserIdAndPostIdAndVideoUrl(Long userId, Long postId, String videoUrl);
}
