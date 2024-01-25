package com.ll.gooHaeYu.domain.jobPost.comment.controller;

import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentDto;
import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentForm;
import com.ll.gooHaeYu.domain.jobPost.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post-comment/{postId}")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> findByPostId(@PathVariable Long postId) {

        return ResponseEntity.ok(commentService.findByPostId(postId));
    }

    @PostMapping("/comment")
    public ResponseEntity<String> write (Authentication authentication,
                                         @PathVariable Long postId,
                                         @Valid @RequestBody CommentForm.Register form){
        Long jobPostId = commentService.writeComment(postId,authentication.getName(),form);

        return ResponseEntity.created(URI.create("/api/job-posts/" + jobPostId)).build();
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> modify (Authentication authentication,
                                          @PathVariable Long postId,
                                          @PathVariable Long commentId,
                                          @Valid @RequestBody CommentForm.Register form) {
        commentService.modifyComment(authentication.getName(), postId, commentId, form);

    return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity delete (Authentication authentication,
                                  @PathVariable Long postId,
                                  @PathVariable Long commentId) {
        commentService.deleteComment(authentication.getName(), postId, commentId);

        return ResponseEntity.noContent().build();
    }
}
