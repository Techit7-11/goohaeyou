package com.ll.gooHaeYu.domain.jobPost.comment.controller;

import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentDto;
import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentForm;
import com.ll.gooHaeYu.domain.jobPost.comment.service.CommentService;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post-comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> findByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByPostId(postId));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<String> write (Authentication authentication,
                                         @PathVariable Long postId,
                                         @Valid @RequestBody CommentForm.Register form){
        Long jobPostId = commentService.writeComment(postId,authentication.getName(),form);

        return ResponseEntity.created(URI.create("/api/job-posts/" + jobPostId)).build();
    }

    @PutMapping("/{postId}/{commentId}")
    public ResponseEntity<Void> modify (Authentication authentication,
                                          @PathVariable Long postId,
                                          @PathVariable Long commentId,
                                          @Valid @RequestBody CommentForm.Register form) {
        commentService.modifyComment(authentication.getName(), postId, commentId, form);

    return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/{commentId}")
    public ResponseEntity delete (Authentication authentication,
                                  @PathVariable Long postId,
                                  @PathVariable Long commentId) {
        commentService.deleteComment(authentication.getName(), postId, commentId);

        return ResponseEntity.noContent().build();
    }
}
