package com.ll.goohaeyou.jobPost.comment.application.dto;

import com.ll.goohaeyou.jobPost.comment.domain.Comment;

import java.util.List;

public record MyCommentResponse(
        Long id,
        String content
) {
    public static MyCommentResponse from(Comment comment) {
        return new MyCommentResponse(
                comment.getId(),
                comment.getContent()
        );
    }

    public static List<MyCommentResponse> convertToList(List<Comment> comments) {
        return comments.stream()
                .map(MyCommentResponse::from)
                .toList();
    }
}
