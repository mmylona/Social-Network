package com.app.linkedinclone.controller;

import com.app.linkedinclone.model.dto.*;
import com.app.linkedinclone.model.enums.ReactionType;
import com.app.linkedinclone.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@CrossOrigin(origins = "https://localhost:3000")
@Slf4j
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ApiGeneralResponse createPost(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestPart("formData") PostDto formData,
                                         @RequestPart(name = "image", required = false) MultipartFile image,
                                         @RequestPart(name = "video", required = false) MultipartFile video,
                                         @RequestPart(name = "voiceNote", required = false) MultipartFile voiceNote) {
        formData.setAuthorEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        formData.setImage(image);
        formData.setVideo(video);
        formData.setVoiceNote(voiceNote);
        return postService.createPost(formData);
    }

    @GetMapping("/{postId}")
    public PostRetrievalResponse getPost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        return postService.getPost(postId);
    }


    @PostMapping("/update")
    public ApiGeneralResponse updatePost(@AuthenticationPrincipal UserDetails userDetails, @RequestPart PostDto postDto) {
        postDto.setAuthorEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/delete/{postId}")
    public ApiGeneralResponse deletePost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        return postService.deletePost(postId);
    }

    @PostMapping("/comment/{postId}")
    public ApiGeneralResponse commentOnPost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId, @RequestBody CommentDto commentDto) {
        return postService.commentOnPost(postId, commentDto, SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    @PostMapping("/reaction/{postId}")
    public ResponseEntity<List<ReactionDto>> addReaction(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId, @RequestBody ReactionType reactionId) {
        return postService.addReaction(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString(), postId, reactionId);
    }

    @GetMapping("/posts")
    public PostRetrievalResponse getPostAndConnectedUsersPost(@AuthenticationPrincipal UserDetails userDetails, @RequestParam int page, @RequestParam int size) {
//        List<Long> recommendedPostIds = postService.recommendPost(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getPostDto().stream().map(PostDto::getId).collect(Collectors.toList());
//       log.info("Recommended Post Ids: {} , with total number of recomended Id: {}", recommendedPostIds,recommendedPostIds.size());
        PostRetrievalResponse response = postService.recommendPost(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        List<Long> postDtoIds = response.getPostDto().stream().map(PostDto::getId).collect(Collectors.toList());
        log.info("PostDto Ids: {}", postDtoIds);
        postService.deleteDuplicateReactions();
//        postService.deleteAllExcept();
        // Find IDs in postDtoIds that are not in recommendedPostIds
//        List<Long> idsNotFoundInRecommended = postDtoIds.stream().filter(id -> !recommendedPostIds.contains(id)).collect(Collectors.toList());

//        log.info("Id's found on postDtos that not found on Recommended is: {}", idsNotFoundInRecommended);


        return response;
    }
}
