package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dto.*;
import com.app.linkedinclone.model.enums.ReactionType;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.awt.print.Pageable;
import java.util.List;
public interface PostService {
    ApiGeneralResponse createPost(PostDto postDto);
    ApiGeneralResponse updatePost(PostDto postDto);
    ApiGeneralResponse deletePost(Long postId);
    ApiGeneralResponse commentOnPost(Long postId, CommentDto commentDto,String authorEmail);
    ResponseEntity<List<ReactionDto>>addReaction(String email, Long postId, ReactionType reactionId);
    @Transactional
    PostRetrievalResponse getPostAndConnectedUsersPost(String email, int page, int size);
    PostRetrievalResponse recommendPost(String userEmail);
    PostRetrievalResponse getPost(Long postId);

    void deleteDuplicateReactions();

    void deleteAllExcept();
}