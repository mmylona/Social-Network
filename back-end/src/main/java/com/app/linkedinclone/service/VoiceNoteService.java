package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dto.VoiceNoteResponse;
import com.app.linkedinclone.model.enums.VoiceNoteType;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface VoiceNoteService {
    @Transactional
    ResponseEntity<VoiceNoteResponse> uploadVoiceNote(String email, MultipartFile voiceNoteFile);
    @Transactional
    ResponseEntity<VoiceNoteResponse> deleteVoiceNote(String email, Long voiceNoteId);
    @Transactional
    String getVoiceNoteUrl(Long voiceNoteId);
}