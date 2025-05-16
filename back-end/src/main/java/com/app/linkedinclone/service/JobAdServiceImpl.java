package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.*;
import com.app.linkedinclone.model.dto.JobAdvertisementDto;
import com.app.linkedinclone.model.dto.UserDto;
import com.app.linkedinclone.model.enums.CommunicationSkills;
import com.app.linkedinclone.model.enums.ProgrammingLanguage;
import com.app.linkedinclone.model.enums.SoftSkills;
import com.app.linkedinclone.model.enums.TechnicalSkills;
import com.app.linkedinclone.repository.JobAdRepository;
import com.app.linkedinclone.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dao.JobAdvertisement.mapToJobAdvertisement;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAdServiceImpl implements JobAdService {
    private static final String JOB_AD_NOT_FOUND = "Job Ad not found";
    private static final String USER_NOT_FOUND = "User not found";
    private final JobAdRepository jobAdRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final FileStorageService fileStorageService;
    private final RecommendationService recommendationService;


    @Override
    public JobAdvertisement createJobAd(String email, JobAdvertisementDto jobAdvertisementDto) {
        log.debug("Creating job ad with title: {}", jobAdvertisementDto.getTitle());
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        JobAdvertisement jobAdvertisement = mapToJobAdvertisement(jobAdvertisementDto);
        jobAdvertisement.setCreatorOf(currentUser);
        return jobAdRepository.save(jobAdvertisement);

    }

    @Override
    public void applyToJobAd(String email, Long jobAdId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        JobAdvertisement jobAdvertisement = jobAdRepository.findById(jobAdId).orElseThrow(() -> new RuntimeException(JOB_AD_NOT_FOUND));
        jobAdvertisement.getApplicants().add(user);
        jobAdRepository.save(jobAdvertisement);
        log.debug("User with email: {} applied to job ad with id: {}", email, jobAdId);
    }

    @Override
    public List<JobAdvertisementDto> getJobAds(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        Skill skill = user.getSkills();
        if (isNull(skill)) {
            return jobAdRepository.findAll().stream().map(JobAdvertisementDto::mapToJobAdvertisementDto).toList();
        } else {
            List<SoftSkills> softSkills = Optional.of(skill.getSoftSkills()).orElseGet(List::of);
            List<TechnicalSkills> technicalSkills = Optional.of(skill.getTechnicalSkills()).orElseGet(List::of);
            List<CommunicationSkills> communicationSkills = Optional.of(skill.getCommunicationSkills()).orElseGet(List::of);
            List<ProgrammingLanguage> programmingLanguages = Optional.of(skill.getProgrammingLanguage()).orElseGet(List::of);
            List<JobAdvertisement> allJobAds = jobAdRepository.findAll();
            log.info(" skill provided");
            // Filter JobAdvertisements based on matching skills
            return allJobAds.stream().filter(jobAd -> {
                Skill jobAdSkills = jobAd.getSkills();
                if (jobAdSkills != null) {
                    // Check for any match in TechnicalSkills, CommunicationSkills, or ProgrammingLanguages
                    boolean matchesTechnicalSkills = !Collections.disjoint(jobAdSkills.getTechnicalSkills(), technicalSkills);
                    boolean matchesCommunicationSkills = !Collections.disjoint(jobAdSkills.getCommunicationSkills(), communicationSkills);
                    boolean matchesProgrammingLanguages = !Collections.disjoint(jobAdSkills.getProgrammingLanguage(), programmingLanguages);

                    return matchesTechnicalSkills || matchesCommunicationSkills || matchesProgrammingLanguages;
                }
                return false;
            }).map(JobAdvertisementDto::mapToJobAdvertisementDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<JobAdvertisementDto> recommendAds(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
        List<JobAdvertisement> recommendations = recommendationService.recommendAdForUser(user,20);

        // Convert JobAdvertisement to JobAdvertisementDto
        return recommendations.stream()
                .map(JobAdvertisementDto::mapToJobAdvertisementDto)
                .collect(Collectors.toList());

    }

    @Override
    public boolean hasAlreadyApplied(String email, Long jobAdId) {
        log.debug("Checking if user with email: {} has already applied to job ad with id: {}", email, jobAdId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        JobAdvertisement jobAdvertisement = jobAdRepository.findById(jobAdId).orElseThrow(() -> new RuntimeException(JOB_AD_NOT_FOUND));
        return jobAdvertisement.getApplicants().contains(user);
    }

    @Override
    public boolean isCreatorOfJobAd(String email, Long jobAdId) {
        log.debug("Checking if user with email: {} is the creator of job ad with id: {}", email, jobAdId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        JobAdvertisement jobAdvertisement = jobAdRepository.findById(jobAdId).orElseThrow(() -> new RuntimeException(JOB_AD_NOT_FOUND));
        User creator = jobAdvertisement.getCreatorOf();
        if (creator == null) {
            log.warn("Job ad with null creator. Job Ad ID: {}", jobAdId);
            return false;
        }
        return creator.equals(user);
    }

    @Override
    public List<UserDto> getApplicants(Long jobAdId) {
        JobAdvertisement jobAdvertisement = jobAdRepository.findById(jobAdId)
                .orElseThrow(() -> new RuntimeException(JOB_AD_NOT_FOUND));

       return jobAdvertisement.getApplicants().stream()
                .map(UserDto::mapToUserDto)
                .map(user -> {
                    FileDocument cvFile = fileStorageService.getFile(user.getId());
                    if(!isNull(cvFile))
                        user.setCvFile(fileStorageService.getFile(user.getId()).getData());
                    user.setProfilePicUrl(imageService.getProfileImageUrl(user.getEmail()));
                    return user;
                })
                .toList();
    }

    @Transactional
    public void trackJobAdView(String email, Long jobAdId) {
        // Find the job advertisement by id
        JobAdvertisement jobAd = jobAdRepository.findById(jobAdId)
                .orElseThrow(() -> new IllegalArgumentException("Job Ad not found"));

        // Find the user who viewed the ad by username
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Add the user to the 'viewers' set if they haven't viewed it already
        if (!jobAd.getViewers().contains(user)) {
            jobAd.getViewers().add(user);
            jobAdRepository.save(jobAd);

        }
        log.info("Job ad {} has been viewed by {} users", jobAdId, jobAd.getViewers().size());
    }

    @Override
    public void deleteJobAd(String username, Long jobAdId) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        JobAdvertisement jobAd = jobAdRepository.findById(jobAdId)
                .orElseThrow(() -> new RuntimeException("Job ad not found"));

        if (!jobAd.getCreatorOf().equals(user)) {
            throw new RuntimeException("User is not the creator of the job ad");
        }

        jobAd.getApplicants().clear();
        jobAdRepository.save(jobAd);
        jobAdRepository.delete(jobAd);
    }

}
