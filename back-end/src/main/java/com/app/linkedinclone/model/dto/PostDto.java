package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class PostDto {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorEmail;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<ReactionDto> reactions;
    private List<CommentDto> comments;
    private String imageUrl;
    private String videoUrl;
    private String voiceNoteUrl;
    private MultipartFile image;
    private MultipartFile video;
    private MultipartFile voiceNote;
    private Long videoId;
    private Long imageId;
    private Long voiceNoteId;
    public static PostDto mapToPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setAuthorId(post.getAuthor().getId());
        postDto.setAuthorName(post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName());
        postDto.setAuthorEmail(post.getAuthor().getEmail());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setCreatedDate(post.getCreatedDate());
        postDto.setUpdatedDate(post.getUpdatedDate());
        postDto.setReactions(post.getReactions().stream().map(ReactionDto::mapToReactionDto).collect(Collectors.toList()));
        postDto.setComments(post.getComments().stream().map(CommentDto::mapToCommentDto).collect(Collectors.toList()));
        postDto.setImageId(post.getImageId());
        postDto.setVideoId(post.getVideoId());
        postDto.setVoiceNoteId(post.getVoiceNoteId());
        return postDto;

    }
}
