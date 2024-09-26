package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.global.event.notification.PostGetInterestedEvent;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.policy.InterestPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.InterestDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDetailDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestService {
    private final JobPostDetailDomainService jobPostDetailDomainService;
    private final ApplicationEventPublisher publisher;
    private final MemberDomainService memberDomainService;
    private final InterestPolicy interestPolicy;
    private final InterestDomainService interestDomainService;

    @Transactional
    public void addInterestToPost(String username, Long postId) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(postId);
        Member member = memberDomainService.getByUsername(username);

        interestPolicy.validateCanInterest(postDetail, member);

        interestDomainService.addInterest(postDetail, member);

        if (!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new PostGetInterestedEvent(this, postDetail, member));
        }
    }

    @Transactional
    public void removeInterestFromPost(String username, Long postId) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(postId);
        Member member = memberDomainService.getByUsername(username);

        interestPolicy.validateRemoveInterest(postDetail, member);

        interestDomainService.removeInterest(postDetail, member);
    }

    public boolean isInterested(String username, Long id) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(id);

        return interestDomainService.isInterested(username, postDetail);
    }
}
