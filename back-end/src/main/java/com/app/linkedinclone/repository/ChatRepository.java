package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Chat;
import com.app.linkedinclone.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByUsersContainingAndUsersContaining(User sender, User recipient);

    Optional<List<Chat>> findByUsersContaining(User user);

}
