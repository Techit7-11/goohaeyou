package com.ll.gooHaeYu.global.event.notification;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostEmployedEvent extends ApplicationEvent {
    private final JobPost jobPost;

    public PostEmployedEvent(Object object, JobPost jobPost) {
        super(object);
        this.jobPost = jobPost;
    }
}
