package com.ll.gooHaeYu.global.event;

import com.ll.gooHaeYu.domain.jobPost.comment.entity.Comment;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.notification.entity.type.ResultTypeCode;
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
