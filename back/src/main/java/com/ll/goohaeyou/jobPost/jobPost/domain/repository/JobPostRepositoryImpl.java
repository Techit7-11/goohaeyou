package com.ll.goohaeyou.jobPost.jobPost.domain.repository;

import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.QJobPost;
import com.ll.goohaeyou.member.member.domain.type.Gender;
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
    public Page<JobPost> findByKw(List<String> kwTypes, String kw, String closed, String gender, int[] min_Ages, List<String> locations, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

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

        // closed 조건 추가
        if (closed.equals("true")) {
            builder.and(QJobPost.jobPost.closed.eq(true));
        } else if (closed.equals("false")) {
            builder.and(QJobPost.jobPost.closed.eq(false));
        } else {
            //전체
        }

        // gender 조건 추가
        if (gender.equals("MALE")) {
            builder.and(QJobPost.jobPost.jobPostDetail.essential.gender.eq(Gender.MALE));
        } else if (gender.equals("FEMALE")) {
            builder.and(QJobPost.jobPost.jobPostDetail.essential.gender.eq(Gender.FEMALE));
        } else {
            //전체
        }

        // 최소 나이 조건 추가
        if (min_Ages != null) {
            List<BooleanExpression> minAgeList = new ArrayList<>();
            for (int min_Age : min_Ages) {
                if (min_Age == 10) {
                    BooleanExpression ageExpression = QJobPost.jobPost.jobPostDetail.essential.minAge.eq(0)
                            .or(QJobPost.jobPost.jobPostDetail.essential.minAge.between(10, 19));
                    minAgeList.add(ageExpression);
                } else if (min_Age == 20) {
                    BooleanExpression ageExpression = QJobPost.jobPost.jobPostDetail.essential.minAge.eq(0)
                            .or(QJobPost.jobPost.jobPostDetail.essential.minAge.between(20, 29));
                    minAgeList.add(ageExpression);
                } else if (min_Age == 30) {
                    BooleanExpression ageExpression = QJobPost.jobPost.jobPostDetail.essential.minAge.eq(0)
                            .or(QJobPost.jobPost.jobPostDetail.essential.minAge.between(30, 39));
                    minAgeList.add(ageExpression);
                } else if (min_Age == 40) {
                    BooleanExpression ageExpression = QJobPost.jobPost.jobPostDetail.essential.minAge.eq(0)
                            .or(QJobPost.jobPost.jobPostDetail.essential.minAge.between(40, 49));
                    minAgeList.add(ageExpression);
                } else if (min_Age == 50) {
                    BooleanExpression ageExpression = QJobPost.jobPost.jobPostDetail.essential.minAge.eq(0)
                            .or(QJobPost.jobPost.jobPostDetail.essential.minAge.between(50, 59));
                    minAgeList.add(ageExpression);
                } else {
                    // 전체
                }
            }

            BooleanExpression combinedMinAgeList = minAgeList.stream()
                    .reduce(BooleanExpression::or)
                    .orElse(null);

            if (combinedMinAgeList != null) {
                builder.and(combinedMinAgeList);
            }
        }

        // 지역 필터링
        if (locations != null) {
            List<BooleanExpression> locationList = new ArrayList<>();
            for (String location : locations) {
                locationList.add(QJobPost.jobPost.location.startsWith(location));
            }
            // 지역 조건 리스트 or 조건으로 결합
            BooleanExpression combinedLocationList = locationList.stream()
                    .reduce(BooleanExpression::or)
                    .orElse(null);
            // 결합된 지역 조건 리스트 쿼리에 적용
            if (combinedLocationList != null) {
                builder.and(combinedLocationList);
            }
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
