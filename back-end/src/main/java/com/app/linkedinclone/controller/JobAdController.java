package com.app.linkedinclone.controller;


import com.app.linkedinclone.model.dto.JobAdvertisementDto;
import com.app.linkedinclone.model.dto.UserDto;
import com.app.linkedinclone.service.JobAdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/job")
@CrossOrigin(origins = "https://localhost:3000")
public class JobAdController {

    private final JobAdService jobAdService;

    @GetMapping("/all")
    public List<JobAdvertisementDto> getJobAds(@AuthenticationPrincipal UserDetails userDetails) {
//        log.info("Recomended job : {}", jobAdService.recommendAds(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()));
        // return ads without matrix factorization
//        return jobAdService.getJobAds(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        //return ads with matrix factorization
        return jobAdService.recommendAds(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

    }

    @PostMapping("/create")
    public void createJobAd(@AuthenticationPrincipal UserDetails userDetails, @RequestBody JobAdvertisementDto jobAdvertisementDto) {
        jobAdService.createJobAd(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), jobAdvertisementDto);
    }

    @PostMapping("/apply/{jobAdId}")
    public void applyToJobAd(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobAdId) {
        jobAdService.applyToJobAd(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), jobAdId);
    }

    @GetMapping("/has-applied/{jobAdId}")
    public boolean hasAlreadyApplied(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobAdId) {
        return jobAdService.hasAlreadyApplied(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), jobAdId);
    }

    @GetMapping("/applicants/{jobAdId}")
    public List<UserDto> getApplicants(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobAdId) {
        return jobAdService.getApplicants(jobAdId);
    }

    @GetMapping("/is-creator/{jobAdId}")
    public boolean isCreatorOfJobAd(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobAdId) {
        return jobAdService.isCreatorOfJobAd(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), jobAdId);
    }

    @PostMapping("/track-view/{jobAdId}")
    public void trackJobAdView(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobAdId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        jobAdService.trackJobAdView(username, jobAdId);
        log.info("User {} viewed job ad {}", username, jobAdId);
    }

    @DeleteMapping("/delete/{jobAdId}")
    public void deleteJobAd(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long jobAdId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        jobAdService.deleteJobAd(username, jobAdId);
        log.info("User {} deleted job ad {}", username, jobAdId);
    }

}
