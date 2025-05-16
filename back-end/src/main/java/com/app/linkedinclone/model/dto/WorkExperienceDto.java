package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.WorkExperience;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WorkExperienceDto {
    Long id;
    private String title;
    @JsonProperty("job_description")
    private String description;
    private String company;
    private String location;
    private Date startDate;
    private Date endDate;
    private int years;

    public static WorkExperienceDto mapToWorkExperienceDto(WorkExperience workExperience) {
        WorkExperienceDto workExperienceDto = new WorkExperienceDto();
        workExperienceDto.setId(workExperience.getId());
        workExperienceDto.setTitle(workExperience.getTitle());
        workExperienceDto.setDescription(workExperience.getDescription());
        workExperienceDto.setCompany(workExperience.getCompany());
        workExperienceDto.setLocation(workExperience.getLocation());
        workExperienceDto.setStartDate(workExperience.getStartDate());
        workExperienceDto.setEndDate(workExperience.getEndDate());
        workExperienceDto.setYears(workExperience.getYears());
        return workExperienceDto;
    }
}
