package com.ll.gooHaeYu.domain.jobPost.jobPost.dto;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.Essential;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.Interest;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPostDetail;
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
    private int minAge = 0;
    private Gender gender = Gender.UNDEFINED;
    private boolean isClosed;
    private String modifiedAt;
    private List<String> interestedUsernames;

    public static JobPostDetailDto fromEntity(JobPost jobPost, JobPostDetail jobPostDetail, Essential essential) {
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
                .gender(jobPostDetail.getEssential().getGender())
                .minAge(jobPostDetail.getEssential().getMinAge())
                .deadLine(jobPost.getDeadline())
                .isClosed(jobPost.isClosed())
                .employed(jobPost.isEmployed())
                .interestedUsernames(interestedUsernames)
                .createdAt(jobPost.getCreatedAt().format(formatter))
                .modifiedAt(mostRecentModifiedDate.format(formatter))
                .build();
    }
}
