package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.VoiceNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceNoteRepository  extends JpaRepository<VoiceNote, Long> {
    VoiceNote findByUserId(Long userId);
    VoiceNote findByPostId(Long postId);
    VoiceNote findByVoiceNoteUri(String voiceNoteUrl);
    VoiceNote findByUserIdAndPostId(Long userId, Long postId);
    VoiceNote findByUserIdAndVoiceNoteUri(Long userId, String voiceNoteUrl);
    VoiceNote findByPostIdAndVoiceNoteUri(Long postId, String voiceNoteUrl);
    VoiceNote findByUserIdAndPostIdAndVoiceNoteUri(Long userId, Long postId, String voiceNoteUrl);
}
