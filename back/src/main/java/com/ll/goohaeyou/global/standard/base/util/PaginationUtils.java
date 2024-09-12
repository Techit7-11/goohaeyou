package com.ll.goohaeyou.global.standard.base.util;

import com.ll.goohaeyou.global.config.AppConfig;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {
    public static Pageable buildPageableWithSorts(List<String> sortBys, List<String> sortOrders, int page) {
        List<Sort.Order> sorts = new ArrayList<>();

        for (int i = 0; i < sortBys.size(); i++) {
            String sortBy = sortBys.get(i);
            String sortOrder = i < sortOrders.size() ? sortOrders.get(i) : "desc"; // 기본값은 desc
            sorts.add(new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy));
        }

        return PageRequest.of(page - 1, AppConfig.getBasePageSize(), Sort.by(sorts));
    }

    public static Pageable buildPageableSortedById(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        return PageRequest.of(page - 1, AppConfig.getBasePageSize(), Sort.by(sorts));
    }

    public static Pageable buildDefaultPageable(int page) {
        return PageRequest.of(page - 1, AppConfig.getBasePageSize());
    }
}
