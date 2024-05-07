package com.ll.gooHaeYu.domain.jobPost.comment.dto;

import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotNull
    private Long id;
    @NotNull
    private Long jobPostId;
    @NotBlank
    private String author;
    @NotBlank
    private String content;
    private String authorProfileImageUrl;
    @NotNull
    private LocalDateTime createAt;
    @NotNull
    private LocalDateTime modifyAt;

    public static CommentDto formEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .jobPostId(comment.getJobPostDetail().getJobPost().getId())
                .author(comment.getMember().getUsername())
                .content(comment.getContent())
                .authorProfileImageUrl(comment.getMember().getProfileImageUrl())
                .createAt(comment.getCreatedAt())
                .modifyAt(comment.getModifiedAt())
                .build();
    }

    public static List<CommentDto> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentDto::formEntity)
                .toList();
    }
}
