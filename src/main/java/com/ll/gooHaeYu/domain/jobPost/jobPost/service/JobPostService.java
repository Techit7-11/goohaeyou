package com.ll.gooHaeYu.domain.jobPost.jobPost.service;

import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.gooHaeYu.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.Interest;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostDetailRepository;
import com.ll.gooHaeYu.domain.jobPost.jobPost.repository.JobPostRepository;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Role;
import com.ll.gooHaeYu.domain.member.member.repository.MemberRepository;
import com.ll.gooHaeYu.domain.member.member.service.MemberService;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostDetailRepository jobPostdetailRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public Long writePost(String username, JobPostForm.Register form) {
        JobPost newPost = JobPost.builder()
                .member(memberService.getMember(username))
                .title(form.getTitle())
                .location(form.getLocation())
                .build();

        JobPostDetail postDetail = JobPostDetail.builder()
                .jobPost(newPost)
                .author(username)
                .body(form.getBody())
                .build();

        jobPostRepository.save(newPost);
        jobPostdetailRepository.save(postDetail);

        return newPost.getId();
    }

    public JobPostDto findById(Long id) {
        JobPost jobPost = findByIdAndValidate(id);
        return JobPostDto.fromEntity(jobPost);
    }

    public List<JobPostDto> findAll() {
        return JobPostDto.toDtoList(jobPostRepository.findAll());
    }

    @Transactional
    public void modifyPost(String username, Long id, JobPostForm.Modify form) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(id);
        if (!canEditPost(username, postDetail.getJobPost().getMember().getUsername()))
            throw new CustomException(NOT_ABLE);

        postDetail.getJobPost().update(form.getTitle(), form.getClosed());
        postDetail.update(form.getBody());
    }

    @Transactional
    public void deletePost(String username, Long id) {
        JobPost post = findByIdAndValidate(id);

        if (!canEditPost(username, post.getMember().getUsername()))
            throw new CustomException(NOT_ABLE);

        jobPostRepository.deleteById(id);
    }

    @Transactional
    public void deleteJobPost(String username, Long postId) {
        JobPost post = findByIdAndValidate(postId);

        Member member = findUserByUserNameValidate(username);
        if (member.getRole() == Role.ADMIN || post.getMember().equals(member)) {
            jobPostRepository.deleteById(postId);
        } else {
            throw new CustomException(NOT_ABLE);
        }
    }

    private Member findUserByUserNameValidate(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }


    public boolean canEditPost(String username, String author) {
        return username.equals(author);
    }

    public JobPost findByIdAndValidate(Long id) {
        return jobPostRepository.findById(id)
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    public JobPostDetail findByJobPostAndNameAndValidate(Long postId) {
        JobPost post = findByIdAndValidate(postId);
        return jobPostdetailRepository.findByJobPostAndAuthor(post,post.getMember().getUsername())
                .orElseThrow(() -> new CustomException(POST_NOT_EXIST));
    }

    @Transactional
    public void Interest(String username, Long postId){
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(postId);
        Member member = memberService.getMember(username);

        if (hasInterest(postDetail,member)) throw new CustomException(NOT_ABLE);

        postDetail.getInterests().add(Interest.builder()
                .jobPostDetail(postDetail)
                .member(member)
                .build());

        postDetail.getJobPost().increaseInterestCount();
    }

    @Transactional
    public void disinterest(String username, Long postId){
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(postId);
        Member member = memberService.getMember(username);

        if (!hasInterest(postDetail,member)) throw new CustomException(NOT_ABLE);

        postDetail.getInterests().removeIf(interest -> interest.getMember().equals(member));
        postDetail.getJobPost().decreaseInterestCount();
    }

    public boolean hasInterest(JobPostDetail post, Member member) {
        return post.getInterests().stream().anyMatch(interest -> interest.getMember().equals(member));
    }

    public List<JobPostDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return JobPostDto.toDtoList(jobPostRepository.findByMemberId(member.getId()));
    }

    public List<JobPostDto> findByInterestAndUsername(Long memberId) {
        return jobPostdetailRepository.findByInterestsMemberId(memberId)
                .stream()
                .map(JobPostDetail::getJobPost)
                .map(JobPostDto::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public void increaseViewCount(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));
        jobPost.increaseViewCount();
    }
}
