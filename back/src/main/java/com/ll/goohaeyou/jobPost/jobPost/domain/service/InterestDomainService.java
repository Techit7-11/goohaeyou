package com.ll.goohaeyou.jobPost.jobPost.domain.service;

import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.Interest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import org.springframework.transaction.annotation.Transactional;

@DomainService
public class InterestDomainService {

    @Transactional
    public void addInterest(JobPostDetail postDetail, Member member) {
        postDetail.getInterests().add(Interest.create(postDetail, member));
        postDetail.getJobPost().increaseInterestCount();
    }

    @Transactional
    public void removeInterest(JobPostDetail postDetail, Member member) {
        postDetail.getInterests().removeIf(interest -> interest.getMember().equals(member));
        postDetail.getJobPost().decreaseInterestCount();
    }

    public boolean isInterested(String username, JobPostDetail postDetail) {
        return postDetail.getInterests().stream()
                .map(Interest::getMember)
                .map(Member::getUsername)
                .anyMatch(username::equals);
    }
}
