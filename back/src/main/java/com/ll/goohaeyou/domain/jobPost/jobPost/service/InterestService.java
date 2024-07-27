package com.ll.goohaeyou.domain.jobPost.jobPost.service;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.Interest;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository.JobPostDetailRepository;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository.JobPostRepository;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.global.event.notification.PostGetInterestedEvent;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.global.exception.jobPost.JobPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestService {
    private final JobPostRepository jobPostRepository;
    private final JobPostDetailRepository jobPostdetailRepository;
    private final MemberService memberService;
    private final ApplicationEventPublisher publisher;
    private final JobPostService jobPostService;

    @Transactional
    public void interest(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Member member = memberService.getMember(username);

        if (hasInterest(postDetail, member)) {
            throw new AuthException.NotAuthorizedException();
        }

        postDetail.getInterests().add(Interest.builder()
                .jobPostDetail(postDetail)
                .member(member)
                .build());

        postDetail.getJobPost().increaseInterestCount();

        if (!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new PostGetInterestedEvent(this, postDetail, member));
        }
    }

    @Transactional
    public void disinterest(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Member member = memberService.getMember(username);

        if (!hasInterest(postDetail, member)) {
            throw new AuthException.NotAuthorizedException();
        }

        postDetail.getInterests().removeIf(interest -> interest.getMember().equals(member));
        postDetail.getJobPost().decreaseInterestCount();
    }

    public boolean hasInterest(JobPostDetail post, Member member) {
        return post.getInterests().stream()
                .anyMatch(interest -> interest.getMember().equals(member));
    }

    public boolean isInterested(String username, Long id) {
        List<String> interestedUsernames = jobPostService.findById(id).getInterestedUsernames();
        return interestedUsernames.stream()
                .anyMatch(username::equals);
    }
}
