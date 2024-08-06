package com.ll.goohaeyou.global.standard.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    JOB_POSTS_BY_CATEGORY("jobPostsByCategory", 20, 200),
    JOB_POSTS_BY_SORT("jobPostsBySort", 60, 50),
    JOB_POSTS_BY_SEARCH("jobPostsBySearch", 60 * 5, 500);

    private final String cacheName;
    private final int expiredAfterWrite;    // 캐시 항목이 쓰여진 후 만료되기까지의 시간(초 단위)
    private final int maximumSize;    // 캐시가 저장할 수 있는 항목의 개수
}
