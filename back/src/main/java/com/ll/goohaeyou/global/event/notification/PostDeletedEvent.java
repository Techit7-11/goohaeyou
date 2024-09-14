package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.domain.jobPost.jobPost.entity.JobPost;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.notification.entity.type.ResultTypeCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostDeletedEvent extends ApplicationEvent {
    private final JobPost jobPost;
    private final Member member;
    private final ResultTypeCode resultTypeCode;

    public PostDeletedEvent(Object object, JobPost jobPost, Member member, ResultTypeCode resultTypeCode) {
        super(object);
        this.jobPost = jobPost;
        this.member = member;
        this.resultTypeCode = resultTypeCode;
    }
}
