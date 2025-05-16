package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Skill findByUserId(Long id);
}
