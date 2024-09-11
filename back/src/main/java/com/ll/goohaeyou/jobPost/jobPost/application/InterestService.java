package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Interest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.member.member.domain.entity.Member;
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
    private final JobPostDetailRepository jobPostDetailRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void addInterestToPost(String username, Long postId) {
        JobPostDetail postDetail = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
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
        JobPostDetail postDetail = jobPostDetailRepository.findById(postId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
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
        JobPostDetail postDetail = jobPostDetailRepository.findById(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        List<String> interestedUsernames = getInterestedUsernames(postDetail);

        return interestedUsernames.stream()
                .anyMatch(username::equals);
    }

    public List<String> getInterestedUsernames(JobPostDetail jobPostDetail) {
        return jobPostDetail.getInterests().stream()
                .map(Interest::getMember)
                .map(Member::getUsername)
                .toList();
    }
}
