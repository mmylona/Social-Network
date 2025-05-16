package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.JobAdvertisement;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.enums.CommunicationSkills;
import com.app.linkedinclone.model.enums.ProgrammingLanguage;
import com.app.linkedinclone.model.enums.SoftSkills;
import com.app.linkedinclone.model.enums.TechnicalSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobAdRepository extends JpaRepository<JobAdvertisement, Long> {
    List<JobAdvertisement> findAllByCreatorOf(User user);
}
