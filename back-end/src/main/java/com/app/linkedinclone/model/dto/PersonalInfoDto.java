package com.app.linkedinclone.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter

public class PersonalInfoDto {
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String title;
    private String location;
    private String connection;
    private String backgroundPicUrl;
    private List<WorkExperienceDto> workExperiences;
    private SkillDto skills;
    private Set<EducationDto> educations;
    private Set<ConnectionResponse> connections;
    @JsonProperty("isWorkExperiencePublic")
    boolean isWorkExperiencePublic;
    @JsonProperty("isSkillPublic")
    boolean isSkillPublic;
    @JsonProperty("isEducationPublic")
    boolean isEducationPublic;

    @Override
    public String toString() {
        return "PersonalInfoDto{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", bio='" + bio + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", connection='" + connection + '\'' +
//                ", backgroundPicUrl='" + backgroundPicUrl + '\'' +
                ", workExperiences=" + workExperiences +
                ", skills=" + skills +
                ", educations=" + educations +
                ", isWorkExperiencePublic=" + isWorkExperiencePublic +
                ", isSkillPublic=" + isSkillPublic +
                ", isEducationPublic=" + isEducationPublic +
                '}';
    }
}
