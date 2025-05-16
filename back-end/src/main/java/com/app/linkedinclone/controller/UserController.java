package com.app.linkedinclone.controller;

import com.app.linkedinclone.model.dao.FileDocument;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.*;
import com.app.linkedinclone.service.FileStorageService;
import com.app.linkedinclone.service.ImageService;
import com.app.linkedinclone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = "https://localhost:3000")
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @PostMapping("/authenticate")
    public LogInResponse authenticate(@RequestBody LoginDto loginDto) {
        LogInResponse ret = userService.authenticate(loginDto);
        log.debug("Authenticating user : {}", ret);
        return ret;
    }

    @PostMapping(value = "/register",consumes = "multipart/form-data")
    public ResponseEntity<?> register(@Valid @ModelAttribute RegisterDto registerDto) {
        log.debug("Registering user with email : {}", registerDto.getEmail());
        return userService.register(registerDto);
    }

    @GetMapping("personal-info")
    public PersonalInfoDto retrievePersonalInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.retrievePersonalInfo((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @PutMapping(value = "personal-info", consumes = "multipart/form-data")
    public PersonalInfoDto updatePersonalInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestPart("personalInfo") PersonalInfoDto personalInfoDto, @RequestPart(value = "image", required = false) MultipartFile image,@RequestPart(value = "cv", required = false) MultipartFile cv) {
        log.debug("Updating personal info for user with PersonalInfoDto  : {}", personalInfoDto);
        log.info("Skills from PersonalInfoDto: {}", personalInfoDto.getSkills());
        return userService.updatePersonalInfo(personalInfoDto, image,cv);
    }


    @GetMapping("/search")
    public List<UserSearchResult> searchUser(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String name) {
        log.debug("Searching user with name : {}", name);
        return userService.searchUser(name, SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    @PostMapping("/update-credentials")
    public ApiGeneralResponse updateCredentials(@AuthenticationPrincipal UserDetails userDetails,@RequestBody CredentialsDto credentialsDto) {
        log.debug("Updating credentials for user with email : {}", credentialsDto.getEmail());
        return userService.updateCredentials(credentialsDto, SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    @GetMapping("cv")
    public ResponseEntity<ByteArrayResource> getCv(@AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Getting cv for user with email : {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return fileStorageService.getCv(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    @GetMapping("skills")
    public List<SkillDto> getSkills(@AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Getting skills for user with email: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return userService.getUserSkills((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }



}