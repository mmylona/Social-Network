package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.enums.ConnectionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dto.SkillDto.mapToSkillDto;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Profile {
    @JsonIgnore
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String company;
    private String title;
    private String profilePic;
    private ConnectionStatus connectionStatus;
    private List<WorkExperienceDto> workExperiences;
    private SkillDto skills;
    Set<EducationDto> educations;
    private Set<UserDto> network;

    public static Profile mapProfile(User user) {

        return Profile.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getFirstName() + " " + user.getLastName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .company(user.getCurrentCompany())
                .title(user.getTitle())
                .profilePic(user.getProfilePicUrl())
                .educations(user.isEducationPublic() ?
                        user.getEducations().stream().map(EducationDto::mapToEducationDto).collect(Collectors.toSet())
                        : null)
                .skills(user.isSkillPublic() ?
                        mapToSkillDto(user.getSkills())
                        : null)
                .workExperiences(user.isWorkExperiencePublic() ?
                        user.getWorkExperiences().stream().map(WorkExperienceDto::mapToWorkExperienceDto).collect(Collectors.toList())
                        : null)
                .build();
    }
}
