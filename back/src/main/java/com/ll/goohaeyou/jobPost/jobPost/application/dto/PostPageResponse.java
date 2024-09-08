package com.ll.goohaeyou.jobPost.jobPost.application.dto;

import com.ll.goohaeyou.global.standard.dto.PageDto;
import org.springframework.lang.NonNull;

public record PostPageResponse(
        @NonNull PageDto<JobPostBasicResponse> itemPage
) {
}
