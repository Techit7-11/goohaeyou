package com.ll.goohaeyou.jobPost.jobPost.dto;

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

    private boolean employed;
    private boolean isClosed;
    private LocalDate deadLine;
    private LocalDate jobStartDate;
}
