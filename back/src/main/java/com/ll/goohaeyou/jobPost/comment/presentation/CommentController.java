package com.ll.goohaeyou.jobPost.comment.presentation;

import com.ll.goohaeyou.global.standard.base.Empty;
import com.ll.goohaeyou.jobPost.comment.application.CommentService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.jobPost.comment.application.dto.CommentResponse;
import com.ll.goohaeyou.jobPost.comment.application.dto.CreateCommentRequest;
import com.ll.goohaeyou.jobPost.comment.application.dto.CreateCommentResponse;
import com.ll.goohaeyou.jobPost.comment.application.dto.ModifyCommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-comment/{postId}")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "구인공고 댓글 API")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "해당 공고에 달린 댓글 목록")
    public ApiResponse<List<CommentResponse>> findByPostId(@PathVariable("postId") Long postId) {

        return ApiResponse.ok(commentService.findByPostId(postId));
    }

    @PostMapping("/comment")
    @Operation(summary = "댓글 작성")
    public ApiResponse<CreateCommentResponse> write (@AuthenticationPrincipal MemberDetails memberDetails,
                                                     @PathVariable("postId") Long postId,
                                                     @Valid @RequestBody CreateCommentRequest request){
        CreateCommentResponse response = commentService.writeComment(postId, memberDetails.getUsername(), request);

        return ApiResponse.ok(response);
    }

    @PutMapping("/comment/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<Empty> modify (@AuthenticationPrincipal MemberDetails memberDetails,
                                         @PathVariable("postId") Long postId,
                                         @PathVariable("commentId") Long commentId,
                                         @Valid @RequestBody ModifyCommentRequest request) {
        commentService.modifyComment(memberDetails.getUsername(), postId, commentId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<Empty> delete (@AuthenticationPrincipal MemberDetails memberDetails,
                                        @PathVariable("postId") Long postId,
                                        @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(memberDetails.getUsername(), postId, commentId);

        return ResponseEntity.noContent().build();
    }
}
