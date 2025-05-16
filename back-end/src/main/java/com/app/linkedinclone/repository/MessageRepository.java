package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<List<Message>> findBySenderEmail(String email);
    Optional<List<Message>> findAllByRecipientEmail(String email);
    Optional<List<Message>> findAllBySenderEmailAndRecipientEmail(String senderEmail, String recipientEmail);
    Optional<Set<Message>> findAllBySenderEmailOrRecipientEmail(String senderEmail, String recipientEmail);

}