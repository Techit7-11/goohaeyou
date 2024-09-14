package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
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
