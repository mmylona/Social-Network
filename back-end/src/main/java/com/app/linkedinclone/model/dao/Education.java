package com.app.linkedinclone.model.dao;


import com.app.linkedinclone.model.dto.EducationDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class Education implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private String grade;
    private String activitiesAndSocieties;
    private String description;
    @JsonBackReference(value = "education")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    public static Education mapToEducation(EducationDto educationDto) {
        Education education = new Education();
        education.setId(educationDto.getId());
        education.setSchoolName(educationDto.getSchoolName());
        education.setDegree(educationDto.getDegree());
        education.setFieldOfStudy(educationDto.getFieldOfStudy());
        education.setStartDate(educationDto.getStartDate());
        education.setEndDate(educationDto.getEndDate());
        education.setGrade(educationDto.getGrade());
        education.setActivitiesAndSocieties(educationDto.getActivitiesAndSocieties());
        education.setDescription(educationDto.getDescription());
        return education;
    }

}