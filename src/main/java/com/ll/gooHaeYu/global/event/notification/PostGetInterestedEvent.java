package com.ll.gooHaeYu.global.event.notification;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
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
