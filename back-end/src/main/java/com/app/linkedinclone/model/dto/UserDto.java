package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dto.SkillDto.mapToSkillDto;
import static java.util.Objects.isNull;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String profilePicUrl;
    private String bio;
    private String title;
    private String location;
    private String backgroundPicUrl;
    boolean isVerified;
    private List<WorkExperienceDto> workExperiences;
    private String currentCompany;
    private String phoneNumber;
    private String userName;
    private byte[] cvFile;
    private Set<EducationDto> educations;
    private SkillDto skills;
    boolean isWorkExperiencePublic;
    boolean isEducationPublic;
    boolean isSkillPublic;
    private List<PostDto> posts;
    private List<CommentDto> comments;
    private Set<ConnectionDto> network;
    private Set<InterestDto> interests;



    @Override
    public String toString(){
        return "{ User : " +
                "id : " + id +
                ", email : " + email +
                ", firstName : " + firstName +
                ",picUrl : " + profilePicUrl +
                ", bio : " + bio +
                ",education : " + educations +
                ",skills : " + skills +
                ", lastName : " + lastName +"}";
    }
    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setProfilePicUrl(user.getProfilePicUrl());
        userDto.setBio(user.getBio());
        userDto.setTitle(user.getTitle());
        userDto.setLocation(user.getLocation());
        userDto.setBackgroundPicUrl(user.getBackgroundPicUrl());
        userDto.setVerified(user.isVerified());
        List<WorkExperience> workExperiences = user.getWorkExperiences();
        userDto.setWorkExperiences(CollectionUtils.isEmpty(workExperiences) ? new ArrayList<>() : workExperiences.stream().map(WorkExperienceDto::mapToWorkExperienceDto).collect(Collectors.toList()));
        userDto.setCurrentCompany(user.getCurrentCompany());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setUserName(user.getFirstName()+ " " + user.getLastName());
        Set<Education> educations = user.getEducations();
        userDto.setEducations(CollectionUtils.isEmpty(educations) ? new HashSet<>() : educations.stream().map(EducationDto::mapToEducationDto).collect(Collectors.toSet()));
        Skill skill = user.getSkills();
        userDto.setSkills(isNull(skill) ? new SkillDto() : mapToSkillDto(skill));
        userDto.setWorkExperiencePublic(user.isWorkExperiencePublic());
        userDto.setEducationPublic(user.isEducationPublic());
        userDto.setSkillPublic(user.isSkillPublic());
        List<Post> posts = user.getPosts();
        userDto.setPosts(CollectionUtils.isEmpty(posts) ? null : posts.stream().map(PostDto::mapToPostDto).collect(Collectors.toList()));
        List<Comment> comments = user.getComments();
        userDto.setComments(CollectionUtils.isEmpty(comments) ? null : comments.stream().map(CommentDto::mapToCommentDto).collect(Collectors.toList()));
        Set<User> network = user.getNetwork();
        userDto.setNetwork(CollectionUtils.isEmpty(network) ? null : network.stream().map(userNet -> new ConnectionDto().mapToConnectionDto(userNet)).collect(Collectors.toSet()));
        Set<Interest> interests = user.getInterests();
        userDto.setInterests(CollectionUtils.isEmpty(interests) ? null : interests.stream().map(InterestDto::mapToInterestDto).collect(Collectors.toSet()));
        return userDto;
    }
}
