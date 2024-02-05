package com.ll.gooHaeYu.domain.jobPost.jobPost.repository;

import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.JobPost;
import com.ll.gooHaeYu.domain.jobPost.jobPost.entity.QJobPost;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class JobPostRepositoryImpl implements JobPostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

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
            System.out.println(orderSpecifier);
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
