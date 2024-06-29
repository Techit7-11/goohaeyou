package com.ll.goohaeyou.global.standard.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
public class PageDto<T> {
    @NonNull
    private long totalElementsCount;
    @NonNull
    private long pageElementsCount;
    @NonNull
    private long totalPagesCount;
    @NonNull
    private int number;
    @NonNull
    private List<T> content;

    public PageDto(Page<T> page) {
        this.totalElementsCount = page.getTotalElements();
        this.pageElementsCount = page.getNumberOfElements();
        this.number = page.getNumber() + 1;
        this.content = page.getContent();
        this.totalPagesCount = page.getTotalPages();
    }
}
