package com.ll.goohaeyou.global.event.notification;

import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentCreatedEvent extends ApplicationEvent {
    private final JobPostDetail jobPostDetail;
    private final Comment comment;

    public CommentCreatedEvent(Object object, JobPostDetail jobPostDetail, Comment comment) {
        super(object);
        this.jobPostDetail = jobPostDetail;
        this.comment = comment;
    }
}
