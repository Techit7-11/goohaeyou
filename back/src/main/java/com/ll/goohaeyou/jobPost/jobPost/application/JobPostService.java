package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.auth.domain.service.UserActivityService;
import com.ll.goohaeyou.category.domain.service.CategoryDomainService;
import com.ll.goohaeyou.category.domain.service.JobPostCategoryDomainService;
import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.global.event.notification.PostDeadlineEvent;
import com.ll.goohaeyou.global.event.notification.PostDeletedEvent;
import com.ll.goohaeyou.global.standard.base.util.PaginationUtils;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.global.standard.dto.PageDto;
import com.ll.goohaeyou.image.domain.service.JobPostImageDomainService;
import com.ll.goohaeyou.jobApplication.domain.service.JobApplicationDomainService;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.*;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.policy.JobPostPolicy;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.EssentialDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDetailDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.JobPostDomainService;
import com.ll.goohaeyou.jobPost.jobPost.domain.service.WageDomainService;
import com.ll.goohaeyou.member.member.domain.service.MemberDomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final ApplicationEventPublisher publisher;
    private final UserActivityService userActivityService;
    private final JobPostPolicy jobPostPolicy;
    private final JobPostCategoryDomainService jobPostCategoryDomainService;
    private final WageDomainService wageDomainService;
    private final EssentialDomainService essentialDomainService;
    private final MemberDomainService memberDomainService;
    private final JobPostDomainService jobPostDomainService;
    private final JobPostDetailDomainService jobPostDetailDomainService;
    private final JobApplicationDomainService jobApplicationDomainService;
    private final JobPostImageDomainService jobPostImageDomainService;
    private final CategoryDomainService categoryDomainService;

    @Transactional
    public void writePost(String username, WriteJobPostRequest request) {
        Member member = memberDomainService.getByUsername(username);
        int regionCode = Util.Region.getRegionCodeFromAddress(request.location());
        JobPost newPost = jobPostDomainService.create(member, request, regionCode);

        jobPostDetailDomainService.create(newPost, username, request);

        jobPostCategoryDomainService.create(newPost.getId(), request.location(), request.categoryId());

        wageDomainService.create(newPost.getId(), request);

        essentialDomainService.create(newPost.getId(), request.minAge(), request.gender());
    }

    public JobPostDetailResponse getJobPostDetail(Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        userActivityService.handleJobPostView(id, httpRequest, httpResponse);

        JobPostDetail postDetail = jobPostDetailDomainService.getById(id);

        return JobPostDetailResponse.from(postDetail.getJobPost(), postDetail, postDetail.getEssential(), postDetail.getWage());
    }

    @Transactional
    public void modifyPost(String username, Long id, ModifyJobPostRequest request) {
        JobPostDetail postDetail = jobPostDetailDomainService.getById(id);
        JobPost jobPost = postDetail.getJobPost();

        jobPostPolicy.validateCanModify(username, jobPost);

        int newRegionCode = Util.Region.getRegionCodeFromAddress(request.location());

        jobPostCategoryDomainService.update(jobPost, request, newRegionCode);

        jobPost.update(request.title(), request.deadLine(), request.jobStartDate(), request.location(), newRegionCode);

        jobPostDetailDomainService.update(postDetail, request);

        jobApplicationDomainService.updateApplicationsOnPostModification(postDetail, request);
    }

    @Transactional
    public void deleteJobPost(String username, Long postId) {
        JobPost post = jobPostDomainService.findById(postId);
        Member member = memberDomainService.getByUsername(username);

        publisher.publishEvent(new PostDeletedEvent(this, post, member, DELETE));

        jobPostPolicy.validateCanDelete(member.getUsername(), post);

        jobPostImageDomainService.deleteByJobPost(post);

        jobPostCategoryDomainService.deleteAll(post);

        jobPostDomainService.delete(postId);
    }

    public List<MyPostResponse> getMyJobPosts(String username) {

        Member member = memberDomainService.getByUsername(username);

        return MyPostResponse.convertToList(jobPostDomainService.getMyJobPosts(member.getId()));
    }

    public List<InterestedJobPostResponse> findMyInterestedPosts(Long memberId) {
        return InterestedJobPostResponse.convertToList(
                jobPostDetailDomainService.getMyInterestedPosts(memberId)
        );
    }

    public Page<JobPostBasicResponse> findByKw(List<String> kwTypes, String kw, String closed, String gender,
                                               int[] min_Age, List<String> location, int page) {

        Pageable pageable = PaginationUtils.buildPageableSortedById(page);

        return JobPostBasicResponse.convertToPage(
                jobPostDomainService.getByKeyword(kwTypes, kw, closed, gender, min_Age, location, pageable)
        );
    }

    @Cacheable(value = "jobPostsBySort", key = "#sortBys + '_' + #sortOrders + '_' + #page", condition = "#page <= 5")
    public JobPostSortPageResponse findBySort(int page, List<String> sortBys, List<String> sortOrders) {

        Pageable pageable = PaginationUtils.buildPageableWithSorts(sortBys, sortOrders, page);

        return new JobPostSortPageResponse(
                new PageDto<>(
                        JobPostBasicResponse.convertToPage(jobPostDomainService.getBySort(pageable))
                )
        );
    }

    @Cacheable(value = "jobPostsBySearch", key = "#titleAndBody + '_' + #titleOnly + '_' + #bodyOnly")
    public List<JobPostBasicResponse> searchJobPostsByTitleAndBody(String titleAndBody, String titleOnly, String bodyOnly) {

        return JobPostBasicResponse.convertToList(
                jobPostDomainService.searchByTitleAndBody(titleAndBody, titleOnly, bodyOnly)
        );
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkAndCloseExpiredJobPosts() {
        jobPostDomainService.publishEventsForExpiredJobPosts(LocalDate.now());
    }

    @Transactional
    public void closeJobPostEarly(String username, Long id) {
        JobPost jobPost = jobPostDomainService.findById(id);

        jobPostPolicy.validateCanClose(username, jobPost);

        jobPost.setDeadlineNull();

        publisher.publishEvent(new PostDeadlineEvent(this, jobPost));
    }

    @Cacheable(value = "jobPostsByCategory", key = "#categoryName + '_' + #page", condition = "#page <= 5")
    public Page<JobPostBasicResponse> getPostsByCategory(String categoryName, int page) {
        Category category = categoryDomainService.getByName(categoryName);
        Pageable pageable = PaginationUtils.buildDefaultPageable(page);

        Page<JobPost> jobPosts = jobPostDomainService.getJobPostsByCategoryId(category.getId(), pageable);

        return JobPostBasicResponse.convertToPage(jobPosts);
    }
}
