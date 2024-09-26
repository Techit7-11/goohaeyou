package com.ll.goohaeyou.jobPost.jobPost.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class InterestPolicy {

    // 관심 추가를 할 수 있는지 검증
    public void validateCanInterest(JobPostDetail postDetail, Member member) {
        if (postDetail.getInterests().stream()
                .anyMatch(interest -> interest.getMember().equals(member))) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    // 관심 해제를 할 수 있는지 검증
    public void validateRemoveInterest(JobPostDetail postDetail, Member member) {
        if (postDetail.getInterests().stream()
                .noneMatch(interest -> interest.getMember().equals(member))) {
            throw new AuthException.NotAuthorizedException();
        }
    }
}
