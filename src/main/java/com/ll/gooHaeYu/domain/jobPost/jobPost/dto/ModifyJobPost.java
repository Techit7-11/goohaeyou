package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.category.entity.Category;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import jakarta.persistence.*;

public class ModifyJobPost {

    private Member member;

    private Category category;

    private String title;

    private String body;

    private boolean isClosed = false;

    public ModifyJobPost (JobPost entity){
        this.member = entity.getMember();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.body = entity.getBody();
        this.isClosed = entity.isClosed();
    }
}
