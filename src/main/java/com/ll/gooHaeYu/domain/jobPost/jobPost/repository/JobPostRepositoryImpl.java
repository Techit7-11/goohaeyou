package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.QJobPost;
import com.ll.gooHaeYu.standard.base.KwType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JobPostRepositoryImpl implements JobPostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<JobPost> findByKw(List<String> kwTypes, String kw, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

//        if (kw != null && !kw.isBlank()) {
//            applyKeywordFilter(kwType, kw, builder);
//        }

        //kw 조건 리스트에 담기
        List<BooleanExpression> kwList = new ArrayList<>();


        if (kwTypes.contains("title")) {
            kwList.add(QJobPost.jobPost.title.containsIgnoreCase(kw));
        }

        if (kwTypes.contains("body")) {
            kwList.add(QJobPost.jobPost.jobPostDetail.body.containsIgnoreCase(kw));
        }

        if (kwTypes.contains("author")) {
            kwList.add(QJobPost.jobPost.member.username.containsIgnoreCase(kw));
        }

        if (kwTypes.contains("location")) {
            kwList.add(QJobPost.jobPost.location.containsIgnoreCase(kw));
        }


        //kw 조건 리스트 or 조건으로 결합
        BooleanExpression combinedKwList = kwList.stream()
                .reduce(BooleanExpression::or)
                .orElse(null);

        //결합된 kw 조건 리스트 쿼리에 적용
        if(combinedKwList != null) {
            builder.and(combinedKwList);
        }

        JPAQuery<JobPost> postsQuery = createPostsQuery(builder);
        applySearchSorting(pageable, postsQuery);

        postsQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        JPAQuery<Long> totalQuery = createTotalQuery(builder)
                .select(QJobPost.jobPost.count())  // Q 클래스 사용
                .from(QJobPost.jobPost)  // Q 클래스 사용
                .where(builder);

        return PageableExecutionUtils.getPage(postsQuery.fetch(), pageable, totalQuery::fetchOne);
    }

    private void applyKeywordFilter(KwType kwType, String kw, BooleanBuilder builder) {
        System.out.println(kw);
        System.out.println("리포지터리 Impl에서 kwType : " + kwType);

        switch (kwType) {
            case kwType.TITLE -> builder.and(QJobPost.jobPost.title.containsIgnoreCase(kw));
            case kwType.BODY -> builder.and(QJobPost.jobPost.jobPostDetail.body.containsIgnoreCase(kw));
            case kwType.AUTHOR -> builder.and(QJobPost.jobPost.member.username.containsIgnoreCase(kw));
            case kwType.LOCATION -> builder.and(QJobPost.jobPost.location.containsIgnoreCase(kw));
            default -> builder.and(
                    QJobPost.jobPost.title.containsIgnoreCase(kw)
                            .or(QJobPost.jobPost.jobPostDetail.body.containsIgnoreCase(kw))
                            .or(QJobPost.jobPost.member.username.containsIgnoreCase(kw))
                            .or(QJobPost.jobPost.location.containsIgnoreCase(kw))
                            //.or(QJobPost.jobPost.employed.eq(Boolean.valueOf(kw)))
            );
        }
    }

    private void applySearchSorting(Pageable pageable, JPAQuery<JobPost> postsQuery) {
        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder<>(JobPost.class, "jobPost");
            postsQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }
    }

    @Override
    public Page<JobPost> findBySort(Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        JPAQuery<JobPost> postsQuery = createPostsQuery(builder);

        applySorting(pageable, postsQuery);

        postsQuery.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPAQuery<Long> totalQuery = createTotalQuery(builder);

        return PageableExecutionUtils.getPage(postsQuery.fetch(), pageable, totalQuery::fetchOne);
    }

    private JPAQuery<JobPost> createPostsQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .selectFrom(QJobPost.jobPost)  // Q 클래스 사용
                .where(builder);
    }

    private void applySorting(Pageable pageable, JPAQuery<JobPost> postsQuery) {
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder<>(JobPost.class, "jobPost");

            OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty())
            );
            postsQuery.orderBy(orderSpecifier);
        }
    }

    private JPAQuery<Long> createTotalQuery(BooleanBuilder builder) {
        return jpaQueryFactory
                .select(QJobPost.jobPost.count())  // Q 클래스 사용
                .from(QJobPost.jobPost)  // Q 클래스 사용
                .where(builder);
    }
}
