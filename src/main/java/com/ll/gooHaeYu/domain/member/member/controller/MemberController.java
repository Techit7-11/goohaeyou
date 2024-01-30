package com.ll.gooHaeYu.domain.member.member.controller;

import com.ll.gooHaeYu.domain.application.application.dto.ApplicationDto;
import com.ll.gooHaeYu.domain.application.application.service.ApplicationService;
import com.ll.gooHaeYu.domain.jobPost.comment.dto.CommentDto;
import com.ll.gooHaeYu.domain.jobPost.comment.service.CommentService;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.service.JobPostService;
import com.ll.gooHaeYu.domain.member.member.dto.JoinForm;
import com.ll.gooHaeYu.domain.member.member.dto.LoginForm;
import com.ll.gooHaeYu.domain.member.member.dto.MemberDto;
import com.ll.gooHaeYu.domain.member.member.dto.MemberForm;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JobPostService jobPostService;
    private final ApplicationService applicationService;
    private final CommentService commentService;

    @PostMapping("/join")
    @Operation(summary = "회원가입")
    public ResponseEntity<String> join(@RequestBody @Valid JoinForm form) {
        Long userId = memberService.join(form);
        return ResponseEntity.created(URI.create("/api/member/join" + userId)).build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<String> login(@RequestBody @Valid LoginForm form) {
        String token = memberService.login(form);
        return ResponseEntity.ok(token);
    }

    @GetMapping
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<MemberDto> detailMember(Authentication authentication) {
        return  ResponseEntity.ok(memberService.findByUsername(authentication.getName()));
    }

    @PutMapping
    @Operation(summary = "내 정보 수정")
    public ResponseEntity<Void> modifyMember(Authentication authentication,
                                                  @Valid @RequestBody MemberForm.Modify form) {
        memberService.modifyMember(authentication.getName(), form);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mypost")
    @Operation(summary = "내 공고 조회")
    public ResponseEntity<List<JobPostDto>> detailMyPost(Authentication authentication) {
        return  ResponseEntity.ok(jobPostService.findByUsername(authentication.getName()));
    }

    @GetMapping("/myapplication")
    @Operation(summary = "내 지원서 조회")
    public ResponseEntity<List<ApplicationDto>> detailMyApplication(Authentication authentication) {
        return  ResponseEntity.ok(applicationService.findByUsername(authentication.getName()));
    }

    @GetMapping("/mycomment")
    @Operation(summary = "내 댓글 조회")
    public ResponseEntity<List<CommentDto>> detailMyComment(Authentication authentication) {
        return  ResponseEntity.ok(commentService.findByUsername(authentication.getName()));
    }
}
