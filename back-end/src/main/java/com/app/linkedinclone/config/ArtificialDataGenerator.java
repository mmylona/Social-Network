package com.app.linkedinclone.config;

import com.app.linkedinclone.model.dao.*;
import com.app.linkedinclone.model.enums.*;

import java.time.LocalDateTime;
import java.util.*;

public class ArtificialDataGenerator {
    private static final Random rand = new Random();
    private static final List<String> postContents = Arrays.asList("This is a great day!", "Exploring new technologies.", "Learning Java is fun.", "Spring Boot makes development easier.", "Just attended a fantastic conference.");
    private static final List<String> commentContents = Arrays.asList("Great post!", "Very informative.", "I agree with this.", "Thanks for sharing.");
    private static final int NUM_ADS = 20;

    public static List<JobAdvertisement> generateAds(List<User> users) {
        List<JobAdvertisement> ads = new ArrayList<>();
        for (int i = 0; i < NUM_ADS; i++) {
            User creator = users.get(rand.nextInt(users.size())); // Randomly select a user as the creator
            Skill skill = new Skill();
            JobAdvertisement ad = new JobAdvertisement();
            ad.setId((long) i);
            ad.setTitle("Ad " + i);
            ad.setDescription("This is a description for Ad " + i);
            ad.setType(JobType.values()[rand.nextInt(JobType.values().length)]); // Random job type
            ad.setLevel(CandidateLevel.values()[rand.nextInt(CandidateLevel.values().length)]); // Random candidate level
            ad.setRemote(rand.nextBoolean()); // Randomly set if the job is remote
            ad.setCreatorOf(creator); // Set the creator
            ad.setCreatedDate(LocalDateTime.now());

            // Assuming each ad targets 1 to 3 skills randomly for simplicity
            List<SoftSkills> targetedSkills = new ArrayList<>();
            for (int j = 0; j < rand.nextInt(3) + 1; j++) {
                targetedSkills.add(SoftSkills.values()[rand.nextInt(SoftSkills.values().length)]);
            }
            List<ProgrammingLanguage> targetedProgrammingLanguages = new ArrayList<>();
            for (int j = 0; j < rand.nextInt(3) + 1; j++) {
                targetedProgrammingLanguages.add(ProgrammingLanguage.values()[rand.nextInt(ProgrammingLanguage.values().length)]);
            }
            List<CommunicationSkills> targetedCommunicationSkills = new ArrayList<>();
            for (int j = 0; j < rand.nextInt(3) + 1; j++) {
                targetedCommunicationSkills.add(CommunicationSkills.values()[rand.nextInt(CommunicationSkills.values().length)]);
            }
            List<TechnicalSkills> targetedTechnicalSkills = new ArrayList<>();
            for (int j = 0; j < rand.nextInt(3) + 1; j++) {
                targetedTechnicalSkills.add(TechnicalSkills.values()[rand.nextInt(TechnicalSkills.values().length)]);
            }
            skill.setSoftSkills(targetedSkills);
            ad.setSkills(skill);
            ad.setApplicants(new HashSet<>()); // Empty set for simplicity
            ad.setViewers(new HashSet<>()); // Empty set for simplicity
            ads.add(ad);
        }
        return ads;
    }

    public static List<User> generateUsers(int numUsers) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < numUsers; i++) {
            Random rand = new Random();
            User user = new User();
            user.setFirstName("User" + (i + 1));
            user.setLastName("Name");

            users.add(user);
        }
        return users;
    }

    public static List<Post> generatePosts(int numPosts, List<User> users) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < numPosts; i++) {
            User author = users.get(getRandomInteger(0, users.size() - 1)); // Assign random author
            // Check if the author is not an admin
            if (author.getRoles().stream().noneMatch(role -> "ADMIN".equals(role.getRoleName()))) {
                Post post = new Post();
                post.setTitle("Post Title " + (i + 1));
                post.setContent("This is some sample post content for post " + (i + 1));
                post.setAuthor(author);
                post.setId(100L + i);
                post.setCreatedDate(LocalDateTime.now()); // Set the current time
                posts.add(post);
            }
        }
        return posts;
    }

    public static List<Reaction> generateReactions(List<User> users, List<Post> posts) {
        List<Reaction> reactions = new ArrayList<>();
        for (User user : users) {
            // Check if the user is not an admin
            if (user.getRoles().stream().noneMatch(role -> "ADMIN".equals(role.getRoleName()))) {
                for (Post post : posts) {
                    if (rand.nextDouble() < 0.2) { // 20% chance of reaction
                        Reaction reaction = new Reaction();
                        reaction.setAuthor(user);
                        reaction.setArticle(post);
                        reaction.setReactionType(ReactionType.values()[rand.nextInt(ReactionType.values().length)]); // Randomly assign a reaction type
                        reaction.setReactionTime(LocalDateTime.now());
                        reactions.add(reaction);
                        post.getReactions().add(reaction);
                    }
                }
            }
        }
        return reactions;
    }

    public static List<Comment> generateComments(List<User> users, List<Post> posts) {
        List<Comment> comments = new ArrayList<>();
        for (User user : users) {
            // Check if the user is not an admin
            if (user.getRoles().stream().noneMatch(role -> "ADMIN".equals(role.getRoleName()))) {
                for (Post post : posts) {
                    if (rand.nextDouble() < 0.05) { // 10% chance of comment
                        Comment comment = new Comment();
                        comment.setContent(commentContents.get(rand.nextInt(commentContents.size())));
                        comment.setAuthor(user);
                        comment.setArticle(post);
                        comment.setCreatedDate(LocalDateTime.now());
                        comment.setUpdatedDate(LocalDateTime.now());
                        comments.add(comment);
                        post.getComments().add(comment);
                    }
                }
            }
        }
        return comments;
    }

    public static void generateConnections(List<User> users) {
        for (User user : users) {
            // Check if the user is not an admin
            if (user.getRoles().stream().noneMatch(role -> "ADMIN".equals(role.getRoleName()))) {
                Set<User> connections = new HashSet<>();
                int numConnections = rand.nextInt(11); // Generate 0 to 10 connections for each user
                for (int i = 0; i < numConnections; i++) {
                    User connection;
                    do {
                        connection = users.get(rand.nextInt(users.size()));
                    } while (connection.equals(user) || connections.contains(connection));
                    connections.add(connection);
                }
                user.setNetwork(connections);
            }
        }
    }

    private static int getRandomInteger(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

}


