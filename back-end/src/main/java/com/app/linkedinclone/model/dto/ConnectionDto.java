package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String profilePicUrl;
    private String title;
    private String location;
    private String email;
    private String currentCompany;
    private String userName;
    private boolean isWorkExperiencePublic;
    private boolean isEducationPublic;
    private boolean isSkillPublic;

    public ConnectionDto mapToConnectionDto(User user) {
        this.userId = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePicUrl = user.getProfilePicUrl();
        this.title = user.getTitle();
        this.location = user.getLocation();
        this.email = user.getEmail();
        this.currentCompany = user.getCurrentCompany();
        this.userName = user.getUsername();
        this.isWorkExperiencePublic = user.isWorkExperiencePublic();
        this.isEducationPublic = user.isEducationPublic();
        this.isSkillPublic = user.isSkillPublic();
        return this;
    }
}
