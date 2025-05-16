package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dao.VoiceNote;
import com.app.linkedinclone.model.dto.VoiceNoteResponse;
import com.app.linkedinclone.repository.UserRepository;
import com.app.linkedinclone.repository.VoiceNoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceNoteServiceImpl implements VoiceNoteService {
    private final VoiceNoteRepository voiceNoteRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<VoiceNoteResponse> uploadVoiceNote(String email, MultipartFile voiceNoteFile) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        VoiceNote voiceNote = new VoiceNote();
        voiceNote.setName(voiceNoteFile.getOriginalFilename());
        voiceNote.setUserId(user.getId());

        try {
            voiceNote.setContent(voiceNoteFile.getBytes());
            voiceNote.setVoiceNoteDuration(2L);
            Long voiceNoteId = voiceNoteRepository.save(voiceNote).getId();

            return ResponseEntity.ok(new VoiceNoteResponse("Voice note uploaded successfully", null, voiceNoteId));
        } catch (Exception e) {
            log.error("Error while uploading voice note: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new VoiceNoteResponse("Voice note upload failed", null, null));
        }
    }

    @Override
    @Transactional
    public ResponseEntity<VoiceNoteResponse> deleteVoiceNote(String email, Long voiceNoteId) {
        VoiceNote voiceNote = voiceNoteRepository.findById(voiceNoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice note not found"));

        try {
            voiceNoteRepository.delete(voiceNote);
            return ResponseEntity.ok(new VoiceNoteResponse("Voice note deleted successfully", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new VoiceNoteResponse("Voice note deletion failed", null, null));
        }
    }

    @Override
    @Transactional
    public String getVoiceNoteUrl(Long voiceNoteId) {
        VoiceNote voiceNote = voiceNoteRepository.findById(voiceNoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voice note not found"));
        return voiceNote.getVoiceNoteUri();
    }
}