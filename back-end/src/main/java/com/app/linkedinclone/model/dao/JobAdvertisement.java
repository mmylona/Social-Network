package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.dto.JobAdvertisementDto;
import com.app.linkedinclone.model.enums.CandidateLevel;
import com.app.linkedinclone.model.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
public class JobAdvertisement extends Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Job type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType type;
    @NotNull(message = "Job level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "job_level")
    private CandidateLevel level;
    @Column(name = "is_remote")
    private boolean isRemote;
    @ManyToOne
//    @JoinColumn(name = "user_id")
    private User creatorOf;
    private LocalDateTime createdDate;


    @OneToOne(cascade = CascadeType.ALL)
    private Skill skills;

    @ManyToMany
    @JoinTable(
            name = "job_advertisement_applicants",
            joinColumns = @JoinColumn(name = "job_advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> applicants = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "job_advertisement_viewers",
            joinColumns = @JoinColumn(name = "job_advertisement_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> viewers = new HashSet<>();

    public JobAdvertisement(){ }

    public static JobAdvertisement mapToJobAdvertisement(JobAdvertisementDto jobAdvertisement) {
        JobAdvertisement jobAdvertisementDto = new JobAdvertisement();
        jobAdvertisementDto.setTitle(jobAdvertisement.getTitle());
        jobAdvertisementDto.setDescription(jobAdvertisement.getDescription());
        jobAdvertisementDto.setCompany(jobAdvertisement.getCompany());
        jobAdvertisementDto.setLocation(jobAdvertisement.getLocation());
        jobAdvertisementDto.setType(jobAdvertisement.getType());
        jobAdvertisementDto.setLevel(jobAdvertisement.getLevel());
        jobAdvertisementDto.setRemote(jobAdvertisement.isRemote());
        jobAdvertisementDto.setSkills(Skill.mapToSkill(jobAdvertisement.getSkills()));
        return jobAdvertisementDto;
    }

}
