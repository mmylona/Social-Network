package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.JobAdvertisement;
import com.app.linkedinclone.model.dto.JobAdvertisementDto;
import com.app.linkedinclone.model.dto.UserDto;

import java.util.List;
public interface JobAdService {
    JobAdvertisement createJobAd(String email,JobAdvertisementDto jobAdvertisementDto);
    void applyToJobAd(String email,Long jobAdId);
    List<JobAdvertisementDto> getJobAds(String email);
    List<JobAdvertisementDto> recommendAds(String email);
    boolean hasAlreadyApplied(String email, Long jobAdId);
    boolean isCreatorOfJobAd(String email, Long jobAdId);
    List<UserDto> getApplicants(Long jobAdId);
    void trackJobAdView(String username, Long jobAdId);
    void deleteJobAd(String username, Long jobAdId);
}
