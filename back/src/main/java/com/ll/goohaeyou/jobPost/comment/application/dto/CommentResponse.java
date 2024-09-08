package com.ll.goohaeyou.jobPost.comment.application.dto;

import com.ll.goohaeyou.jobPost.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        Long jobPostId,
        String author,
        String content,
        String authorProfileImageUrl,
        LocalDateTime createAt,
        LocalDateTime modpackage
) {

    public static CommentResponse form(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getJobPostDetail().getJobPost().getId(),
                comment.getMember().getUsername(),
                comment.getContent(),
                comment.getMember().getProfileImageUrl(),
                comment.getCreatedAt(),
                comment.getModifiedAt()
        );
    }

    public static List<CommentResponse> convertToList(List<Comment> comments) {
        return comments.stream()
                .map(CommentResponse::form)
                .toList();
    }
}
