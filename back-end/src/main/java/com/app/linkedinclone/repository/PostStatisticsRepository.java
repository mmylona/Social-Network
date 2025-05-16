package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.PostStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostStatisticsRepository  extends JpaRepository<PostStatistics, Long> {
    Optional<PostStatistics> findByUserIdAndPostId(Long userId, Long postId);
}
