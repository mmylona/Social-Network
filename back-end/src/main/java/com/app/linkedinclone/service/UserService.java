package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.Role;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public interface UserService {
    LogInResponse authenticate(LoginDto loginDto);

    ResponseEntity<?> register(@Valid RegisterDto registerDto);

    Role saveRole(Role role);

    User saverUser(User user);

    PersonalInfoDto retrievePersonalInfo(String email);

    @Transactional
    PersonalInfoDto updatePersonalInfo(PersonalInfoDto personalInfoDto, MultipartFile image, MultipartFile cv);

    List<UserSearchResult> searchUser(String userName,String email);
    List<UserDto> getAllUsers();
    User getUserById(Long id);
    Resource exportUsers(String format, ExportRequest exportRequests);
    ApiGeneralResponse updateCredentials(CredentialsDto credentialsDto,String currentEmail);

    List<SkillDto> getUserSkills(String email);
}