package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.QJobPost;
import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
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
    public Page<JobPost> findByKw(List<String> kwTypes, String kw, String closed, String gender, int min_Age, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

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
        switch (min_Age) {
            case 10:
                builder.and(QJobPost.jobPost.jobPostDetail.essential.minAge.lt(20)
                        .and(QJobPost.jobPost.jobPostDetail.essential.minAge.goe(10)));
                break;
            case 20:
                builder.and(QJobPost.jobPost.jobPostDetail.essential.minAge.lt(30)
                        .and(QJobPost.jobPost.jobPostDetail.essential.minAge.goe(20)));
                break;
            case 30:
                builder.and(QJobPost.jobPost.jobPostDetail.essential.minAge.lt(40)
                        .and(QJobPost.jobPost.jobPostDetail.essential.minAge.goe(30)));
                break;
            case 40:
                builder.and(QJobPost.jobPost.jobPostDetail.essential.minAge.lt(50)
                        .and(QJobPost.jobPost.jobPostDetail.essential.minAge.goe(40)));
                break;
            case 50:
                builder.and(QJobPost.jobPost.jobPostDetail.essential.minAge.lt(100)
                        .and(QJobPost.jobPost.jobPostDetail.essential.minAge.goe(50)));
                break;
            default:
                //전체
                break;
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
