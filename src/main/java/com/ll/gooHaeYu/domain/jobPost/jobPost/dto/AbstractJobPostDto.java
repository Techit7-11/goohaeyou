package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.type.WageType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@Getter
public class AbstractJobPostDto {
    @NotNull
    private Long id;
    @NotNull
    private String author;
    @NotNull
    private String title;
    @NotNull
    private String location;
    @NotNull
    private long commentsCount;
    @NotNull
    private long incrementViewCount;
    @NotNull
    private long interestsCount;
    @NotNull
    private String createdAt;
    @NotNull
    private String workTime;
    @NotNull
    private int cost;
    @NotNull
    private WageType wageType;

    private boolean employed;
    private boolean isClosed;
    private LocalDate deadLine;
}
