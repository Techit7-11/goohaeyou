package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.domain.Interest;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.global.event.notification.PostGetInterestedEvent;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostService jobPostService;

    @Transactional
    public void addInterestToPost(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        if (hasInterest(postDetail, member)) {
            throw new AuthException.NotAuthorizedException();
        }

        postDetail.getInterests().add(
                Interest.create(postDetail, member)
        );

        postDetail.getJobPost().increaseInterestCount();

        if (!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new PostGetInterestedEvent(this, postDetail, member));
        }
    }

    @Transactional
    public void removeInterestFromPost(String username, Long postId) {
        JobPostDetail postDetail = jobPostService.findByJobPostAndNameAndValidate(postId);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

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
