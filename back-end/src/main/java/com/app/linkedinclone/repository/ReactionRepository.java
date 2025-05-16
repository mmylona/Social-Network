package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
}