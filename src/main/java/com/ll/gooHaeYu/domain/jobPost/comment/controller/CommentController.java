package com.ll.gooHaeYu.domain.jobPost.comment.controller;

import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentDto;
import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentForm;
import com.ll.gooHaeYu.domain.jobPost.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Comment", description = "구인공고 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post-comment/{postId}")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "해당 공고에 달린 댓글 목록")
    public ResponseEntity<List<CommentDto>> findByPostId(@PathVariable Long postId) {

        return ResponseEntity.ok(commentService.findByPostId(postId));
    }

    @PostMapping("/comment")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<String> write (Authentication authentication,
                                         @PathVariable("postId") Long postId,
                                         @Valid @RequestBody CommentForm.Register form){
        Long jobPostId = commentService.writeComment(postId,authentication.getName(),form);

        return ResponseEntity.created(URI.create("/api/job-posts/" + jobPostId)).build();
    }

    @PutMapping("/comment/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<Void> modify (Authentication authentication,
                                        @PathVariable Long postId,
                                        @PathVariable Long commentId,
                                        @Valid @RequestBody CommentForm.Register form) {
        commentService.modifyComment(authentication.getName(), postId, commentId, form);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity delete (Authentication authentication,
                                  @PathVariable("postId") Long postId,
                                  @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(authentication.getName(), postId, commentId);

        return ResponseEntity.noContent().build();
    }
}
