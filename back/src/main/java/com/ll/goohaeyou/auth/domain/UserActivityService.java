package com.ll.goohaeyou.auth.domain;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.infra.util.CookieUtil;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityService {
    private final JobPostRepository jobPostRepository;

    @Transactional
    public void handleJobPostView(Long jobPostId, HttpServletRequest request, HttpServletResponse response) {
        final String VIEWED_JOB_POSTS_COOKIE = "viewedJobPosts";
        boolean isJobPostAlreadyVisited = checkJobPostVisited(request, jobPostId, VIEWED_JOB_POSTS_COOKIE);

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        if (!isJobPostAlreadyVisited) {
            jobPost.increaseViewCount();
            addOrUpdateViewedJobPostsCookie(request, response, jobPostId, VIEWED_JOB_POSTS_COOKIE);
        }
    }

    // 방문 여부 확인 (쿠키를 활용)
    private boolean checkJobPostVisited(HttpServletRequest request, Long jobId, String cookieName) {
        Cookie viewCookie = CookieUtil.findCookie(request, cookieName);
        return viewCookie != null && viewCookie.getValue().contains("_" + jobId);
    }

    // 쿠키 추가
    private void addOrUpdateViewedJobPostsCookie(HttpServletRequest request, HttpServletResponse response,
                                                 Long jobId, String cookieName) {
        // 쿠키에서 방문한 게시물 ID 목록 가져옴
        Cookie viewCookie = CookieUtil.findCookie(request, cookieName);
        String newCookieValue;

        // 가져온 목록에 현재 게시물 ID 추가
        if (viewCookie != null) {
            String existingValue = viewCookie.getValue();
            if (!existingValue.contains("_" + jobId)) {
                newCookieValue = existingValue + "_" + jobId;
            } else {
                // 이미 방문한 게시물이면 쿠키 값을 변경 X
                return;
            }
        } else {
            newCookieValue = "_" + jobId;
        }

        // 새로운 쿠키 값 설정 or 기존 쿠키 업데이트
        CookieUtil.addCookie(response, cookieName, newCookieValue, 24 * 60 * 60); // 24시간
    }
}
