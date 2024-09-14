package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostGetInterestedEvent extends ApplicationEvent {
    private final JobPostDetail jobPostDetail;
    private final Member member;

    public PostGetInterestedEvent(Object object, JobPostDetail jobPostDetail, Member member) {
        super(object);
        this.jobPostDetail = jobPostDetail;
        this.member = member;
    }
}
