package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Education;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EducationDto {
    private Long id;
    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private String grade;
    private String activitiesAndSocieties;
    private String description;
    private String userEmail;

    public static EducationDto mapToEducationDto(Education education) {
        EducationDto educationDto = new EducationDto();
        educationDto.setId(education.getId());
        educationDto.setSchoolName(education.getSchoolName());
        educationDto.setDegree(education.getDegree());
        educationDto.setFieldOfStudy(education.getFieldOfStudy());
        educationDto.setStartDate(education.getStartDate());
        educationDto.setEndDate(education.getEndDate());
        educationDto.setGrade(education.getGrade());
        educationDto.setActivitiesAndSocieties(education.getActivitiesAndSocieties());
        educationDto.setDescription(education.getDescription());
        educationDto.setUserEmail(education.getUser().getEmail());
        return educationDto;
    }
}
