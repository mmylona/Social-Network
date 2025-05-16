package com.app.linkedinclone.service;

import com.app.linkedinclone.exception.ExportException;
import com.app.linkedinclone.model.dao.*;
import com.app.linkedinclone.model.dto.*;
import com.app.linkedinclone.model.enums.RoleName;
import com.app.linkedinclone.model.enums.UserFields;
import com.app.linkedinclone.repository.ImageRepository;
import com.app.linkedinclone.repository.RoleRepository;
import com.app.linkedinclone.repository.SkillRepository;
import com.app.linkedinclone.repository.UserRepository;
import com.app.linkedinclone.util.JwtUtilities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dto.SkillDto.mapToSkillDto;
import static com.app.linkedinclone.model.enums.ImageType.PROFILE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper = new XmlMapper();
    private final SkillRepository skillRepository;
    private final FileStorageService fileStorageService;
    private final BiFunction<User, ExportRequest, Map<String, Object>> userToMap = this::mapUserToExportData;

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public User saverUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public PersonalInfoDto retrievePersonalInfo(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (isNull(user))
            return new PersonalInfoDto();

        PersonalInfoDto personalInfoDto = new PersonalInfoDto();
        setPersonalInfoFields(user, personalInfoDto);
        setSkills(user, personalInfoDto);
        setWorkExperiences(user, personalInfoDto);
        setEducations(user, personalInfoDto);
        setBackgroundImage(user, personalInfoDto);
        setConnections(user, personalInfoDto);

        log.debug("Personal info to return to retrieve : {}", personalInfoDto);
        return personalInfoDto;
    }

    @Override
    @Transactional
    public PersonalInfoDto updatePersonalInfo(PersonalInfoDto personalInfoDto, MultipartFile image, MultipartFile cv) {
        User user = userRepository.findByEmail(personalInfoDto.getEmail()).orElse(null);
        log.info("Updating data: {}", personalInfoDto);
        if (!isNull(user)) {
            updateUserFields(user, personalInfoDto);
            user.setSkills(checkAndPopulateSkills(personalInfoDto, user.getId()));
            updateWorkExperiences(user, personalInfoDto);
            updateEducations(user, personalInfoDto);
            updateImage(user, image);
            updateCv(user, cv);
            userRepository.save(user);
        }
        return personalInfoDto;
    }

    @Override
    public List<UserSearchResult> searchUser(String userName, String email) {
        List<User> users = userRepository.findAllByUserNameContainingIgnoreCase(userName);
        log.info("Search results : {}", users);
        log.info("All users : {}", userRepository.findAll());
        log.info("Current user : {}", email);
        return users.stream()
                .filter(user -> !user.getEmail().equals(email))
                .map(this::mapToUserSearchResult)
                .toList();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::mapToUserDto).toList();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Resource exportUsers(String format, ExportRequest exportRequests) {
        List<User> userList = userRepository.findAllByIdIn(exportRequests.getUsers());
        List<Map<String, Object>> exportData = userList.stream()
                .map(user -> userToMap.apply(user, exportRequests))
                .toList();

        String result;
        try {
            result = "json".equalsIgnoreCase(format) ? jsonMapper.writeValueAsString(exportData) : xmlMapper.writeValueAsString(exportData);
        } catch (JsonProcessingException e) {
            throw new ExportException(e.getMessage());
        }

        return new ByteArrayResource(result.getBytes());
    }

    @Override
    public ApiGeneralResponse updateCredentials(CredentialsDto credentialsDto, String currentEmail) {
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (passwordEncoder.matches(credentialsDto.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(credentialsDto.getNewPassword()));
            userRepository.save(user);
            return new ApiGeneralResponse("Password updated successfully", HttpStatus.OK);
        } else {
            return new ApiGeneralResponse("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> register(@Valid RegisterDto registerDto) {
        log.info("Registering user with email : {}", registerDto.getEmail());
        if (TRUE.equals(userRepository.existsByEmail(registerDto.getEmail()))) {
            log.error("Email is already taken !");
            return new ResponseEntity<>("Email is already taken !", HttpStatus.SEE_OTHER);
        }
        User user = createUser(registerDto);
        userRepository.save(user);
        String token = jwtUtilities.generateToken(registerDto.getEmail(), Collections.singletonList(user.getRoles().get(0).getRoleName()));
        log.info("Saving user with email : {}", registerDto.getEmail());
        uploadProfileImage(registerDto, user);
        return new ResponseEntity<>(new BearerToken(token, "Bearer "), HttpStatus.OK);
    }

    @Override
    public LogInResponse authenticate(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return createLogInResponse(user);
    }

    private void setPersonalInfoFields(User user, PersonalInfoDto personalInfoDto) {
        personalInfoDto.setFirstName(user.getFirstName());
        personalInfoDto.setLastName(user.getLastName());
        personalInfoDto.setEmail(user.getEmail());
        personalInfoDto.setBio(user.getBio());
        personalInfoDto.setTitle(user.getTitle());
        personalInfoDto.setLocation(user.getLocation());
        personalInfoDto.setEducationPublic(user.isEducationPublic());
        personalInfoDto.setSkillPublic(user.isSkillPublic());
        personalInfoDto.setWorkExperiencePublic(user.isWorkExperiencePublic());
    }

    private void setSkills(User user, PersonalInfoDto personalInfoDto) {
        log.info("Skills : {}", user.getSkills());
        if (!isNull(user.getSkills())) {
            personalInfoDto.setSkills(mapToSkillDto(user.getSkills()));
        }
    }

    private void setWorkExperiences(User user, PersonalInfoDto personalInfoDto) {
        if (!CollectionUtils.isEmpty(user.getWorkExperiences())) {
            personalInfoDto.setWorkExperiences(
                    user.getWorkExperiences().stream()
                            .map(WorkExperienceDto::mapToWorkExperienceDto)
                            .toList()
            );
        }
    }

    private void setEducations(User user, PersonalInfoDto personalInfoDto) {
        if (!CollectionUtils.isEmpty(user.getEducations())) {
            personalInfoDto.setEducations(
                    user.getEducations().stream()
                            .map(EducationDto::mapToEducationDto)
                            .collect(Collectors.toSet())
            );
        }
    }

    private void setBackgroundImage(User user, PersonalInfoDto personalInfoDto) {
        Image image = imageRepository.findByUserIdAndTypeIs(user.getId(), PROFILE);
        if (!isNull(image)) {
            personalInfoDto.setBackgroundPicUrl(image.getImageUri());
        }
    }

    private void setConnections(User user, PersonalInfoDto personalInfoDto) {
        Set<User> userNetwork = user.getNetwork();
        personalInfoDto.setConnections(!userNetwork.isEmpty() ?
                userNetwork.stream().map(
                        u -> {
                            Image img = imageRepository.findByUserIdAndTypeIs(u.getId(), PROFILE);
                            return new ConnectionResponse(u.getId(), u.getFirstName() + " " + u.getLastName(),
                                    u.getCurrentCompany(), u.getTitle(), !isNull(img) ? img.getImageUri() : null);
                        }
                ).collect(Collectors.toSet()) : Collections.emptySet()
        );
    }

    private void updateUserFields(User user, PersonalInfoDto personalInfoDto) {
        user.setFirstName(personalInfoDto.getFirstName());
        user.setLastName(personalInfoDto.getLastName());
        user.setBio(personalInfoDto.getBio());
        user.setUserName(personalInfoDto.getFirstName() + " " + personalInfoDto.getLastName());
        user.setTitle(personalInfoDto.getTitle());
        user.setLocation(personalInfoDto.getLocation());
        user.setEducationPublic(personalInfoDto.isEducationPublic());
        user.setSkillPublic(personalInfoDto.isSkillPublic());
        user.setWorkExperiencePublic(personalInfoDto.isWorkExperiencePublic());
    }


    private void updateWorkExperiences(User user, PersonalInfoDto personalInfoDto) {
        if (!CollectionUtils.isEmpty(personalInfoDto.getWorkExperiences())) {
            List<WorkExperience> workExperiences = personalInfoDto.getWorkExperiences().stream()
                    .map(WorkExperience::mapToWorkExperience)
                    .toList();
            user.getWorkExperiences().addAll(workExperiences);
        } else {
            user.setWorkExperiences(null);
        }
    }

    private void updateEducations(User user, PersonalInfoDto personalInfoDto) {
        if (!CollectionUtils.isEmpty(personalInfoDto.getEducations())) {
            Set<Education> educations = personalInfoDto.getEducations().stream()
                    .map(Education::mapToEducation)
                    .peek(edu -> edu.setUser(user))
                    .collect(Collectors.toSet());
            log.info("Educations -- updatePersonalInfo  : {}", educations);
            user.setEducations(educations);
        } else {
            user.setEducations(null);
        }
    }

    private void updateImage(User user, MultipartFile image) {
        if (!isNull(image) && !image.isEmpty()) {
            imageService.uploadImage(user.getEmail(), image, PROFILE);
        }
    }

    private void updateCv(User user, MultipartFile cv) {
        if (!isNull(cv) && !cv.isEmpty()) {
            FileDocument fileDocument = fileStorageService.getFile(user.getId());
            try {
                if (isNull(fileDocument)) {
                    fileStorageService.storeFile(cv, user.getId());
                } else {
                    fileStorageService.updateFile(user.getId(), cv);
                }
            } catch (IOException e) {
                log.error("Error while storing/updating cv : {}", e.getMessage());
            }
        }
    }

    private UserSearchResult mapToUserSearchResult(User user) {
        UserSearchResult userSearchResult = new UserSearchResult();
        userSearchResult.setFirstName(user.getFirstName());
        userSearchResult.setLastName(user.getLastName());
        userSearchResult.setEmail(user.getEmail());
        return userSearchResult;
    }

    private Map<String, Object> mapUserToExportData(User user, ExportRequest exportRequests) {
        UserDto userDto = UserDto.mapToUserDto(user);
        var userData = new HashMap<String, Object>();
        userData.put("id", user.getId());
        List<String> fields = exportRequests.getFields();
        log.info("Exporting fields: {}", UserFields.FIRSTNAME.getFieldName());
        if (fields.contains(UserFields.POSTS.getFieldName())) {
            userData.put(UserFields.POSTS.getFieldName(), Optional.ofNullable(userDto.getPosts()).orElse(Collections.emptyList()));
        }
        if (fields.contains(UserFields.EXPERIENCE.name())) {
            userData.put(UserFields.EXPERIENCE.getFieldName(), Optional.ofNullable(userDto.getWorkExperiences()).orElse(Collections.emptyList()));
        }
        if (fields.contains(UserFields.NOTES.getFieldName())) {
            userData.put(UserFields.NOTES.getFieldName(), Optional.ofNullable(userDto.getInterests()).orElse(Collections.emptySet()));
        }
        if (fields.contains(UserFields.COMMENTS.getFieldName())) {
            userData.put(UserFields.COMMENTS.getFieldName(), Optional.ofNullable(userDto.getComments()).orElse(Collections.emptyList()));
        }
        if (fields.contains(UserFields.NETWORK.getFieldName())) {
            userData.put(UserFields.NETWORK.getFieldName(), Optional.ofNullable(userDto.getNetwork()).orElse(Collections.emptySet()));
        }
        if (fields.contains(UserFields.EMAIL.getFieldName())) {
            userData.put(UserFields.EMAIL.getFieldName(), userDto.getEmail());
        }
        if (fields.contains(UserFields.BIO.getFieldName())) {
            userData.put(UserFields.BIO.getFieldName(), userDto.getBio());
        }
        if (fields.contains(UserFields.PHONE.getFieldName())) {
            userData.put(UserFields.PHONE.getFieldName(), userDto.getPhoneNumber());
        }
        if (fields.contains(UserFields.ADDRESS.getFieldName())) {
            userData.put(UserFields.ADDRESS.getFieldName(), userDto.getLocation());
        }
        if (fields.contains(UserFields.TITLE.getFieldName())) {
            userData.put(UserFields.TITLE.getFieldName(), userDto.getTitle());
        }
        if (fields.contains(UserFields.CONNECTIONS.getFieldName())) {
            userData.put(UserFields.CONNECTIONS.getFieldName(), userDto.getNetwork());
        }
        if (fields.contains(UserFields.EDUCATION.getFieldName())) {
            userData.put(UserFields.EDUCATION.getFieldName(), userDto.getEducations());
        }
        if (fields.contains(UserFields.SKILLS.getFieldName())) {
            userData.put(UserFields.SKILLS.getFieldName(), userDto.getSkills());
        }
        if (fields.contains(UserFields.FIRSTNAME.getFieldName())) {
            userData.put(UserFields.FIRSTNAME.getFieldName(), userDto.getFirstName());
        }
        if (fields.contains(UserFields.LASTNAME.getFieldName())) {
            userData.put(UserFields.LASTNAME.getFieldName(), userDto.getLastName());
        }
        return userData;
    }

    private User createUser(RegisterDto registerDto) {
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setUserName(registerDto.getFirstName() + " " + registerDto.getLastName());
        Role role = roleRepository.findByRoleName(RoleName.USER);
        log.info("Saving role : {}", role.getRoleName());
        user.setRoles(Collections.singletonList(role));
        return user;
    }

    private void uploadProfileImage(RegisterDto registerDto, User user) {
        if (!isNull(registerDto.getProfilePicture()) && !registerDto.getProfilePicture().isEmpty()) {
            imageService.uploadImage(registerDto.getEmail(), registerDto.getProfilePicture(), PROFILE);
        } else {
            if (imageService.initUserDefaultImage(user)) {
                log.info("Default image set for user with email : {}", registerDto.getEmail());
            } else {
                log.error("Default image not set for user with email : {}", registerDto.getEmail());
            }
        }
    }

    private LogInResponse createLogInResponse(User user) {
        LogInResponse logInResponse = new LogInResponse();
        logInResponse.setUserName(user.getFirstName() + " " + user.getLastName());
        logInResponse.setEmail(user.getEmail());
        logInResponse.setUserRole(user.getRoles().get(0));
        List<String> rolesNames = new ArrayList<>();
        user.getRoles().forEach(r -> rolesNames.add(r.getRoleName()));
        logInResponse.setToken(jwtUtilities.generateToken(user.getUsername(), rolesNames));
        return logInResponse;
    }

    private Skill checkAndPopulateSkills(PersonalInfoDto personalInfoDto, Long userId) {
        Skill skill = skillRepository.findByUserId(userId);
        if (isNull(skill)) {
            skill = new Skill();
        }
        skill.setTechnicalSkills(Optional.ofNullable(personalInfoDto.getSkills().getTechnicalSkills()).orElseGet(List::of));
        skill.setCommunicationSkills(Optional.ofNullable(personalInfoDto.getSkills().getCommunicationSkills()).orElseGet(List::of));
        skill.setProgrammingLanguage(Optional.ofNullable(personalInfoDto.getSkills().getProgrammingLanguages()).orElseGet(List::of));
        skill.setSoftSkills(Optional.ofNullable(personalInfoDto.getSkills().getSoftSkills()).orElseGet(List::of));
        log.info("Updating skills for user with id {}: {}", userId, skill);

        return skill;
    }

    @Override
    public List<SkillDto> getUserSkills(String email){
        log.info("Email: {}",email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            // User not found, return an empty list or throw an exception
            log.info("User not found");
            return Collections.emptyList();
        }
        User user = optionalUser.get();
        Skill skill = user.getSkills();
        if (skill == null) {
            // Skill not found for the user, return an empty list or throw an exception
            log.info("No skills found for user with email: {}", email);
            return Collections.emptyList();
        }
        log.info("Skills for user with email {}: {}", email, skill);

        return SkillDto.fromSkill(skill);
    }
}