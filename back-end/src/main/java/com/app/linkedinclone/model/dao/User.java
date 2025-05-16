package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.enums.InteractionType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;


@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "First Name is required")
    private String firstName;
    @NotNull(message = "Last Name is required")
    private String lastName;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;    // Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character
    private String profilePicUrl;
    private String bio;
    private String title;
    private String location;
    private String backgroundPicUrl;
    @Column(name = "is_verified")
    boolean isVerified;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    List<Role> roles;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<WorkExperience> workExperiences;
    private String currentCompany;
    private String phoneNumber;
    private String userName;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Education> educations;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "skill_id", referencedColumnName = "id")
    private Skill skills;
    boolean isAdministrator = false;
    boolean isWorkExperiencePublic;
    boolean isEducationPublic;
    boolean isSkillPublic;
    @ManyToMany(mappedBy = "users")
    private List<Chat> chats;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    @ManyToMany
    @JoinTable(
            name = "connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connected_user_id")
    )
    private Set<User> network;

    @ManyToMany
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<Interest> interests;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CV cv;
    @ManyToMany(mappedBy = "viewers")
    private Set<JobAdvertisement> viewedJobAds = new HashSet<>();

    public User() {
        this.posts = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.network = Set.of();
        this.interests = Set.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
        return authorities;
    }

    public User(String email, String password, List<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
//                ", backgroundPicUrl='" + backgroundPicUrl + '\'' +
                ", isVerified=" + isVerified +
                ", roles=" + roles +
//                ", workExperiences=" + workExperiences +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
//                ", educations=" + educations +
                ", skills=" + skills +
                ", isAdministrator=" + isAdministrator +
                ", isWorkExperiencePublic=" + isWorkExperiencePublic +
                ", isEducationPublic=" + isEducationPublic +
                ", isSkillPublic=" + isSkillPublic +
                '}';
    }


    public List<UserItemInteraction> getUserItemInteractions(){
        List<UserItemInteraction> userItemInteractions = new ArrayList<>();
        for (Post post : posts) {
            if(!CollectionUtils.isEmpty(post.getReactions())){
                for (Reaction reaction : post.getReactions()) {
                    UserItemInteraction userItemInteraction = new UserItemInteraction();
                    userItemInteraction.setUser(reaction.getAuthor());
                    userItemInteraction.setPost(post);

                    // Check the type of reaction and set the InteractionType accordingly
                    switch (reaction.getReactionType()) {
                        case LIKE:
                            userItemInteraction.setInteractionType(InteractionType.LIKE);
                            break;
                        case LOVE:
                            userItemInteraction.setInteractionType(InteractionType.LOVE);
                            break;
                        case CARE:
                            userItemInteraction.setInteractionType(InteractionType.CARE);
                            break;
                        default:
                            break;
                    }

                    userItemInteractions.add(userItemInteraction);
                }
            }
            if(!CollectionUtils.isEmpty(post.getComments())){
                for (Comment comment : post.getComments()) {
                    UserItemInteraction userItemInteraction = new UserItemInteraction();
                    userItemInteraction.setUser(this);
                    userItemInteraction.setPost(post);
                    userItemInteraction.setInteractionType(InteractionType.COMMENT);
                    userItemInteractions.add(userItemInteraction);
                }
            }

        }
        return userItemInteractions;
    }

    public void setUserItemInteractions( List<UserItemInteraction> interactions){
        for(UserItemInteraction interaction: interactions){
            if(interaction.getInteractionType().equals(InteractionType.LIKE)){
                Reaction reaction = new Reaction();
                reaction.setAuthor(this);
                reaction.setArticle(interaction.getPost());
                interaction.getPost().getReactions().add(reaction);
            }else if(interaction.getInteractionType().equals(InteractionType.COMMENT)){
                Comment comment = new Comment();
                comment.setAuthor(this);
                comment.setArticle(interaction.getPost());
                interaction.getPost().getComments().add(comment);
            }
        }
    }
}
