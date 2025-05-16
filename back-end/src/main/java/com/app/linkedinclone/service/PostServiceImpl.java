package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.*;
import com.app.linkedinclone.model.dto.*;
import com.app.linkedinclone.model.enums.ApiResponseStatus;
import com.app.linkedinclone.model.enums.ReactionType;
import com.app.linkedinclone.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.app.linkedinclone.model.dto.PostDto.mapToPostDto;
import static com.app.linkedinclone.model.enums.ApiResponseStatus.SUCCESS;
import static com.app.linkedinclone.model.enums.ImageType.POST;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_FOUND_ID = "Post not found with id: ";
    private static final String POST_NOT_FOUND = "Post not found";
    private static final int NUM_FEATURES = 10;
    private static final double LAMBDA = 0.1;
    private static final int NUM_ITERATIONS = 10;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final VideoService videoService;
    private final ImageService imageService;
    private final VoiceNoteService voiceNoteService;
    private final PostStatisticsRepository postStatisticsRepository;
    private final ImageRepository imageRepository;
    private final VoiceNoteRepository voiceNoteRepository;
    private final VideoRepository videoRepository;
    private final RecommendationService recommendationService;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;


    @Override
    public ApiGeneralResponse createPost(PostDto postDto) {

        Optional<User> authorOpt = userRepository.findByEmail(postDto.getAuthorEmail());
        if (authorOpt.isEmpty()) {
            log.error("User not found with id: {}", postDto.getAuthorEmail());
            return new ApiGeneralResponse("User not found", BAD_REQUEST);
        }
        log.debug("Creating post for user with email: {} , and content: {}", postDto.getAuthorEmail(), postDto);
        User author = authorOpt.get();
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setCreatedDate(LocalDateTime.now());
        handleMediaUpload(postDto, post);
        author.getPosts().add(post);
        userRepository.save(author);
        log.debug("Post created successfully and saved in the database.");
        return new ApiGeneralResponse("Post created successfully", OK);

    }

    private void handleMediaUpload(PostDto postDto, Post post) {
        if (postDto.getImage() != null) {
            ResponseEntity<ImageResponse> response = imageService.uploadImage(postDto.getAuthorEmail(), postDto.getImage(), POST);
            log.debug("Uploading image for post with id: {}", response.getBody().getImageId());
            if (response.getStatusCode().equals(OK)) {
                post.setImageId(Objects.requireNonNull(response.getBody()).getImageId());
            }
        }
        if (postDto.getVideo() != null) {
            ResponseEntity<VideoResponse> response = videoService.uploadVideo(postDto.getAuthorEmail(), postDto.getVideo());
            if (response.getStatusCode().equals(OK)) {
                post.setVideoId(Objects.requireNonNull(response.getBody()).getVideoId());
            }
        }
        if (postDto.getVoiceNote() != null) {
            log.debug("Uploading voice note for post with id: {}", post.getId());
            ResponseEntity<VoiceNoteResponse> response = voiceNoteService.uploadVoiceNote(postDto.getAuthorEmail(), postDto.getVoiceNote());
            if (response.getStatusCode().equals(OK)) {
                post.setVoiceNoteId(Objects.requireNonNull(response.getBody()).getVoiceNoteId());
            }
        }

    }

    @Override
    public ApiGeneralResponse updatePost(PostDto postDto) {
        log.debug("Updating post with id: {}", postDto.getId());
        Optional<Post> postOptional = postRepository.findById(postDto.getId());
        if (postOptional.isEmpty()) {
            log.error(POST_NOT_FOUND_ID + "{}", postDto.getId());
            return new ApiGeneralResponse(POST_NOT_FOUND, INTERNAL_SERVER_ERROR);
        }
        Post post = postOptional.get();
        post.setContent(postDto.getContent());
        post.setUpdatedDate(LocalDateTime.now());
        postRepository.save(post);
        return new ApiGeneralResponse("Post updated successfully", OK);
    }

    @Override
    public ApiGeneralResponse deletePost(Long postId) {
        log.debug("Deleting post with id: {}", postId);
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.error(POST_NOT_FOUND_ID + "{}", postId);
            return new ApiGeneralResponse(POST_NOT_FOUND, INTERNAL_SERVER_ERROR);
        }
        Post post = postOptional.get();
        postRepository.delete(post);
        return new ApiGeneralResponse("Post deleted successfully", OK);
    }

    @Override
    public ApiGeneralResponse commentOnPost(Long postId, CommentDto commentDto, String authorEmail) {

        log.debug("Adding comment to post with id: {}", postId);
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> authorOptional = userRepository.findByEmail(authorEmail);
        if (postOptional.isEmpty() || authorOptional.isEmpty()) {
            log.error(POST_NOT_FOUND_ID + "{}", postId);
            return new ApiGeneralResponse(POST_NOT_FOUND, INTERNAL_SERVER_ERROR);
        }
        Post post = postOptional.get();
        User author = authorOptional.get();
        post.getComments().add(new Comment(commentDto.getContent(), author, post, LocalDateTime.now(), null));
        postRepository.save(post);
        PostStatistics postStatistics = postStatisticsRepository.findByUserIdAndPostId(author.getId(), postId).orElseGet(() -> {
            PostStatistics statistics = new PostStatistics();
            statistics.setPost(post);
            statistics.setUser(author);
            return statistics;
        });
        postStatistics.setUserComments(postStatistics.getUserComments() + 1);
        postStatisticsRepository.save(postStatistics);
        return new ApiGeneralResponse("Comment added successfully", OK);
    }

    @Override
    public ResponseEntity<List<ReactionDto>> addReaction(String email, Long postId, ReactionType reactionType) {
        log.debug("Adding reaction to post with id: {}", postId);
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> reactionAuthor = userRepository.findByEmail(email);
        if (postOptional.isEmpty() || reactionAuthor.isEmpty()) {
            log.error(POST_NOT_FOUND_ID + "{}", postId);
            return ResponseEntity.badRequest().build();
        }
        Post post = postOptional.get();

        List<Reaction> reactions = post.getReactions();

        reactions.stream().filter(r -> r.getAuthor().getEmail().equals(email)).findFirst().ifPresentOrElse(
                r -> {
                    if (!reactionType.getReaction().equals(r.getReactionType().getReaction())) {
                        log.debug("Removing reaction for user with email: {}", email);
                        r.setReactionType(reactionType);
                    } else {
                        log.debug("Reaction already exists for user with email: {}", email);
                    }

                },
                () -> {
                    Reaction reaction = new Reaction(reactionAuthor.get(), reactionType, LocalDateTime.now(), post);
                    post.getReactions().add(reaction);
                }
        );
        post.setReactions(reactions);
        postRepository.save(post);
        return ResponseEntity.ok(post.getReactions().stream().map(ReactionDto::mapToReactionDto).toList());
    }

    @Override
    @Transactional
    public PostRetrievalResponse getPostAndConnectedUsersPost(String email, int page, int size) {
        log.debug("Fetching post and connected users post for email: {}", email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.error("User not found with email: {}", email);
            return new PostRetrievalResponse("User not found", ApiResponseStatus.ERROR, null, 0, 0);
        }
        User user = userOptional.get();
        Set<User> connectedUsers = user.getNetwork();
        Pageable pageable = PageRequest.ofSize(size).withPage(page).withSort(Sort.by("createdDate").descending());
        Page<Post> connectedUsersPosts = postRepository.findAllByAuthorAndNetwork(user, connectedUsers, pageable);
        List<PostStatistics> postStatistics = connectedUsersPosts.getContent().stream().map(post -> postStatisticsRepository.findByUserIdAndPostId(user.getId(), post.getId())
                .map(statistics -> {
                    statistics.setUserViews(statistics.getUserViews() + 1L);
                    return statistics;
                })
                .orElseGet(() -> {
                    PostStatistics newStatistics = new PostStatistics();
                    newStatistics.setPost(post);
                    newStatistics.setUser(user);
                    newStatistics.setUserViews(1L); // Initialize with 1 view
                    return newStatistics;
                })).map(postStatisticsRepository::save).collect(Collectors.toList());
        postStatisticsRepository.saveAll(postStatistics);
        return new PostRetrievalResponse("Post and connected users post fetched successfully", SUCCESS, connectedUsersPosts.getContent().stream()
                .map(post -> {
                    String imageUrl = null;
                    if (!isNull(post.getImageId())) {
                        Optional<Image> image = imageRepository.findById(post.getImageId());
                        if (image.isPresent())
                            imageUrl = image.get().getImageUri();
                    }
                    String videoUrl = post.getVideoId() != null ? videoService.getVideoUrl(post.getVideoId()) : null;
                    String voiceNoteUrl = post.getVoiceNoteId() != null ? "data:audio/mp3;base64," + Base64.getEncoder().encodeToString(voiceNoteRepository.getReferenceById(post.getVoiceNoteId()).getContent()) : null;
                    PostDto postDto = new PostDto().mapToPostDto(post);
                    postDto.setImageUrl(imageUrl);
                    postDto.setVideoUrl(videoUrl);
                    postDto.setVoiceNoteUrl(voiceNoteUrl);
                    return postDto;
                })
                .toList(), connectedUsersPosts.getTotalElements(), connectedUsersPosts.getTotalPages());
    }


    @Override
    public PostRetrievalResponse getPost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        log.info("Fetching post with id: {}", postId);
        if (postOptional.isEmpty()) {
            log.error(POST_NOT_FOUND_ID + "{}", postId);
            return new PostRetrievalResponse(POST_NOT_FOUND, ApiResponseStatus.ERROR, null, 0, 0);
        }
        Post post = postOptional.get();
        String imageUrl = null;
        if (!isNull(post.getImageId())) {
            Optional<Image> image = imageRepository.findById(post.getImageId());
            if (image.isPresent())
                imageUrl = image.get().getImageUri();
        }
        String videoUrl = post.getVideoId() != null ? videoService.getVideoUrl(post.getVideoId()) : null;
        String voiceNoteUrl = post.getVoiceNoteId() != null ? "data:audio/mp3;base64," + Base64.getEncoder().encodeToString(voiceNoteRepository.getReferenceById(post.getVoiceNoteId()).getContent()) : null;
        PostDto postDto = mapToPostDto(post);
        log.info("Post content :{}", postDto.getContent());
        postDto.setImageUrl(imageUrl);
        postDto.setVideoUrl(videoUrl);
        postDto.setVoiceNoteUrl(voiceNoteUrl);
        return new PostRetrievalResponse("Post fetched successfully", SUCCESS, List.of(postDto), 1, 1);
    }

    @Override
    public PostRetrievalResponse recommendPost(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
        List<Post> recommendations = recommendationService.recommendPostForUser(user,20);

        // Map recommendations to PostDto and fetch associated media (images, videos, voice notes)
        List<PostDto> postDtos = recommendations.stream().map(post -> {
            PostDto postDto = new PostDto().mapToPostDto(post);

            // Fetch image URL if it exists
            String imageUrl = post.getImageId() != null ? imageRepository.findById(post.getImageId()).map(Image::getImageUri).orElse(null) : null;

            // Fetch video URL if it exists
            String videoUrl = post.getVideoId() != null ? videoService.getVideoUrl(post.getVideoId()) : null;

            // Fetch voice note URL if it exists
            String voiceNoteUrl = post.getVoiceNoteId() != null
                    ? "data:audio/mp3;base64," + Base64.getEncoder().encodeToString(voiceNoteRepository.getReferenceById(post.getVoiceNoteId()).getContent())
                    : null;

            // Set the media URLs in the PostDto
            postDto.setImageUrl(imageUrl);
            postDto.setVideoUrl(videoUrl);
            postDto.setVoiceNoteUrl(voiceNoteUrl);

            return postDto;
        }).toList();

        // Return the response with the mapped PostDtos
        return new PostRetrievalResponse("Recommended posts fetched successfully", SUCCESS, postDtos, postDtos.size(), 1);
    }

    public void deleteDuplicateReactions() {
        // Get all posts
        List<Post> allPosts = postRepository.findAll();

        for (Post post : allPosts) {
            // Group reactions by author
            Map<User, List<Reaction>> groupedReactions = post.getReactions().stream()
                    .collect(Collectors.groupingBy(Reaction::getAuthor));

            // For each author, if there are more than one reactions, delete the duplicates
            for (Map.Entry<User, List<Reaction>> entry : groupedReactions.entrySet()) {
                if (entry.getValue().size() > 1) {
                    log.info("DUPLICATE");
                    // Keep the first reaction in the list
                    Reaction firstReaction = entry.getValue().get(0);

                    // Remove all reactions of this author from the post's reactions list
                    List<Reaction> duplicateReactions = post.getReactions().stream()
                            .filter(reaction -> reaction.getAuthor().equals(entry.getKey()) && !reaction.equals(firstReaction))
                            .collect(Collectors.toList());
                    post.getReactions().removeAll(duplicateReactions);

                    // Delete the duplicate reactions from the database
                    reactionRepository.deleteAll(duplicateReactions);
                }
            }

            // Save the post back to the database
            postRepository.save(post);
        }
    }

    public void deleteAllExcept() {
        List<User> usersToKeep = new ArrayList<>();
        String[] emails = {
                "marina@marina.com",
                "marina2@marina2.com",
                "marina3@marina3.com",
                "marina4@marina4.com",
                "marina5@marina2.com",
                "marina6@marina6.com",
                "marina7@marina7.com",
                "admin@gmail.com"
        };

        for (String email : emails) {
            userRepository.findByEmail(email).ifPresent(usersToKeep::add);
        }

        // Delete all users except those in the list
        List<User> users = userRepository.findAll();
        users.removeAll(usersToKeep);
        userRepository.deleteAll(users);

        // Delete all posts except those of the users in the list
        List<Post> posts = postRepository.findAll().stream()
                .filter(post -> !usersToKeep.contains(post.getAuthor()))
                .collect(Collectors.toList());
        postRepository.deleteAll(posts);

        // Delete all reactions except those of the users in the list
        List<Reaction> reactions = reactionRepository.findAll().stream()
                .filter(reaction -> !usersToKeep.contains(reaction.getAuthor()))
                .collect(Collectors.toList());
        reactionRepository.deleteAll(reactions);

        // Delete all comments except those of the users in the list
        List<Comment> comments = commentRepository.findAll().stream()
                .filter(comment -> !usersToKeep.contains(comment.getAuthor()))
                .collect(Collectors.toList());
        commentRepository.deleteAll(comments);

        // Remove all connections except those of the users in the list
        for (User user : usersToKeep) {
            user.getNetwork().retainAll(usersToKeep);
            userRepository.save(user);
        }
    }


}
