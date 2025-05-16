package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.JobAdvertisement;
import com.app.linkedinclone.model.enums.CandidateLevel;
import com.app.linkedinclone.model.enums.JobType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dto.SkillDto.mapToSkillDto;

@Getter
@Setter
public class JobAdvertisementDto {
    private Long id;
    @NotNull(message = "Job title is required")
    private String title;
    @NotNull(message = "Job description is required")
    private String description;
    @NotNull(message = "Company name is required")
    private String company;
    @NotNull(message = "Job location is required")
    private String location;
    @NotNull(message = "Job type is required")
    private JobType type; // e.g., Full-time, Part-time, Contract, etc.
    @NotNull(message = "Job level is required")
    private CandidateLevel level; // e.g., Entry, Mid, Senior, etc.
    private boolean isRemote; // Whether the job is remote or not
    private SkillDto skills;
    private Set<UserDto> applicants;
    private UserDto creatorOf;

    public static JobAdvertisementDto mapToJobAdvertisementDto(JobAdvertisement jobAdvertisement) {
        JobAdvertisementDto jobAdvertisementDto = new JobAdvertisementDto();
        jobAdvertisementDto.setId(jobAdvertisement.getId());
        jobAdvertisementDto.setTitle(jobAdvertisement.getTitle());
        jobAdvertisementDto.setDescription(jobAdvertisement.getDescription());
        jobAdvertisementDto.setCompany(jobAdvertisement.getCompany());
        jobAdvertisementDto.setLocation(jobAdvertisement.getLocation());
        jobAdvertisementDto.setType(jobAdvertisement.getType());
        jobAdvertisementDto.setLevel(jobAdvertisement.getLevel());
        jobAdvertisementDto.setRemote(jobAdvertisement.isRemote());
        jobAdvertisementDto.setSkills(mapToSkillDto(jobAdvertisement.getSkills()));
        jobAdvertisementDto.setApplicants(jobAdvertisement.getApplicants().stream().map(UserDto::mapToUserDto).collect(Collectors.toSet()));
        return jobAdvertisementDto;
    }
}
