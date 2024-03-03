package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.*;
import com.ll.gooHaeYu.domain.member.member.entity.Member;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuperBuilder
@Getter
public class JobPostDetailDto extends AbstractJobPostDto{
    @NotNull
    private String body;
    private long applicationCount;
    private int minAge;
    private Gender gender;
    private String modifiedAt;
    private List<String> interestedUsernames;
    private int deposit;

    public static JobPostDetailDto fromEntity(JobPost jobPost, JobPostDetail jobPostDetail, Essential essential, Wage wage) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        List<String> interestedUsernames = jobPostDetail.getInterests().stream()
                .map(Interest::getMember)
                .map(Member::getUsername)
                .toList();
        LocalDateTime mostRecentModifiedDate = jobPost.getModifiedAt().isAfter(jobPostDetail.getModifiedAt()) ? jobPost.getModifiedAt() : jobPostDetail.getModifiedAt();

        return JobPostDetailDto.builder()
                .id(jobPost.getId())
                .author(jobPostDetail.getAuthor())
                .title(jobPost.getTitle())
                .body(jobPostDetail.getBody())
                .location(jobPost.getLocation())
                .incrementViewCount(jobPost.getIncrementViewCount())
                .commentsCount(jobPost.getCommentsCount())
                .applicationCount(jobPost.getApplicationCount())
                .interestsCount(jobPost.getInterestsCount())
                .gender(essential.getGender())
                .minAge(essential.getMinAge())
                .deadLine(jobPost.getDeadline())
                .isClosed(jobPost.isClosed())
                .employed(jobPost.isEmployed())
                .interestedUsernames(interestedUsernames)
                .cost(wage.getCost())
                .wageType(wage.getWageType())
                .workTime(wage.getWorkTime())
                .deposit(jobPostDetail.getDeposit())
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .modifiedAt(mostRecentModifiedDate.format(formatter))
                .build();
    }
}
