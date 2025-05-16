package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.Post;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.UserInteraction;
import com.app.linkedinclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;

    @Override
    public List<UserInteraction> getNotifications(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user's username is null or empty
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            // Update the username
            // Generate a unique email
            Instant instant = Instant.now();
            String uniqueEmail = "defaultUsername" + instant.getEpochSecond() + "@default.com";
            // Set the unique email
            user.setEmail(uniqueEmail);
            // Save the user back to the database
            userRepository.save(user);
        }

        List<Post> posts = user.getPosts();
        List<UserInteraction> interactions = posts.stream()
                .map(Post::getUserInteractions)
                .flatMap(List::stream)
                .filter(interaction -> interaction.getUserName() != null && !interaction.getUserName().equals(user.getUsername()))
                .toList();
        log.info("Returning {} notifications for user: {} , reactions{}", interactions.size(), email, interactions);
        return interactions;
    }
}
