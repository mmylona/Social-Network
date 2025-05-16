package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.dto.WorkExperienceDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "work_experience")
public class WorkExperience extends Job {
    private Date startDate;
    private Date endDate;
    private int years;

    public static WorkExperience mapToWorkExperience(WorkExperienceDto workExperienceDto) {
        WorkExperience workExperience = new WorkExperience();
        workExperience.setId(workExperienceDto.getId());
        workExperience.setTitle(workExperienceDto.getTitle());
        workExperience.setDescription(workExperienceDto.getDescription());
        workExperience.setCompany(workExperienceDto.getCompany());
        workExperience.setLocation(workExperienceDto.getLocation());
        workExperience.setStartDate(workExperienceDto.getStartDate());
        workExperience.setEndDate(workExperienceDto.getEndDate());
        workExperience.setYears(workExperienceDto.getYears());
        return workExperience;
    }

    @Override
    public boolean equals(Object o) {
        WorkExperience that = (WorkExperience) o;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.company, that.company) &&
                Objects.equals(this.location, that.location) &&
                Objects.equals(this.startDate, that.startDate) &&
                Objects.equals(this.endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, company, location, startDate, endDate);
    }
}
