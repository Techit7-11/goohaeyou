package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.global.standard.dto.PageDto;
import org.springframework.lang.NonNull;

public record JobPostSortPageResponse(
        @NonNull PageDto<JobPostBasicResponse> itemPage
) {
}
