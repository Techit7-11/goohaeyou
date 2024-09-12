package com.ll.goohaeyou.jobPost.jobPost.domain;

import com.ll.goohaeyou.category.domain.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.global.event.notification.PostDeadlineEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.jobPost.comment.domain.Comment;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.ModifyJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.WriteJobPostRequest;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostSpecifications;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostDomainService {
    private final JobPostRepository jobPostRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostCategoryRepository jobPostCategoryRepository;

    @Transactional
    public JobPost create(Member member, WriteJobPostRequest request, int regionCode) {
        JobPost newPost = JobPost.create(
                member,
                request.title(), request.location(),
                request.deadLine(), request.jobStartDate(), regionCode);

        return jobPostRepository.save(newPost);
    }

    @Transactional
    public void update(JobPost jobPost, ModifyJobPostRequest request, int newRegionCode) {
        jobPost.update(request.title(), request.deadLine(), request.jobStartDate(), request.location(), newRegionCode);
    }

    @Transactional
    public void delete(Long postId) {
        jobPostRepository.deleteById(postId);
    }

    @Transactional
    public void addCommentToPost(JobPostDetail postDetail, Comment comment) {
        postDetail.getComments().add(comment);
        postDetail.getJobPost().increaseCommentsCount();
    }

    public JobPost findById(Long id) {
        return jobPostRepository.findById(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
    }

    public List<JobPost> getMyJobPosts(Long memberId) {
        return jobPostRepository.findByMemberId(memberId);
    }

    public Page<JobPost> getByKeyword(List<String> kwTypes, String kw, String closed, String gender,
                                      int[] min_Age, List<String> location, Pageable pageable) {

        return jobPostRepository.findByKw(kwTypes, kw, closed, gender, min_Age, location, pageable);
    }

    public Page<JobPost> getBySort(Pageable pageable) {
        return jobPostRepository.findBySort(pageable);
    }

    public List<JobPost> searchByTitleAndBody(String titleAndBody, String titleOnly, String bodyOnly) {
        Specification<JobPost> spec = Specification.where(null);

        spec = applyTitleAndBodySearch(spec, titleAndBody);
        spec = applyTitleOnlySearch(spec, titleOnly);
        spec = applyBodyOnlySearch(spec, bodyOnly);

        return jobPostRepository.findAll(spec);
    }

    public void publishEventsForExpiredJobPosts(LocalDate currentDate) {
        List<JobPost> expiredJobPosts = jobPostRepository.findByClosedFalseAndDeadlineBefore(currentDate);

        for (JobPost jobPost : expiredJobPosts) {
            publisher.publishEvent(new PostDeadlineEvent(this, jobPost));
        }
    }

    public Page<JobPost> getJobPostsByCategoryId(Long categoryId, Pageable pageable) {
        return jobPostCategoryRepository.findJobPostsByCategoryId(categoryId, pageable);
    }

    private Specification<JobPost> applyTitleAndBodySearch(Specification<JobPost> spec, String titleAndBody) {
        if (titleAndBody != null) {
            return spec.and(JobPostSpecifications.titleContains(titleAndBody))
                    .or(JobPostSpecifications.bodyContains(titleAndBody));
        }
        return spec;
    }

    private Specification<JobPost> applyTitleOnlySearch(Specification<JobPost> spec, String titleOnly) {
        if (titleOnly != null) {
            return spec.and(JobPostSpecifications.titleContains(titleOnly));
        }
        return spec;
    }

    private Specification<JobPost> applyBodyOnlySearch(Specification<JobPost> spec, String bodyOnly) {
        if (bodyOnly != null) {
            return spec.and(JobPostSpecifications.bodyContains(bodyOnly));
        }
        return spec;
    }
}
