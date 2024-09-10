package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.auth.domain.UserActivityService;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.domain.JobPostCategory;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.category.domain.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.category.domain.type.CategoryType;
import com.ll.goohaeyou.global.config.AppConfig;
import com.ll.goohaeyou.global.event.notification.ChangeOfPostEvent;
import com.ll.goohaeyou.global.event.notification.PostDeadlineEvent;
import com.ll.goohaeyou.global.event.notification.PostDeletedEvent;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.base.RegionType;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.global.standard.dto.PageDto;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.*;
import com.ll.goohaeyou.jobPost.jobPost.domain.*;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.*;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.POST_MODIFICATION;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostDetailRepository jobPostdetailRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final ImageStorage imageStorage;
    private final CategoryRepository categoryRepository;
    private final JobPostCategoryRepository jobPostCategoryRepository;
    private final WageRepository wageRepository;
    private final EssentialRepository essentialRepository;
    private final UserActivityService userActivityService;

    @Transactional
    public Long writePost(String username, WriteJobPostRequest request) {
        int regionCode = Util.Region.getRegionCodeFromAddress(request.location());

        JobPost newPost = createAndSaveJobPost(username, request, regionCode);
        createAndSaveJobPostDetail(newPost, username, request);

        return newPost.getId();
    }

    public JobPostDetailResponse getJobPostDetail(Long id, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        userActivityService.handleJobPostView(id, httpRequest, httpResponse);
        JobPostDetail postDetail = jobPostdetailRepository.findById(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);

        return JobPostDetailResponse.from(postDetail.getJobPost(), postDetail, postDetail.getEssential(), postDetail.getWage());
    }

    private JobPost createAndSaveJobPost(String username, WriteJobPostRequest request, int regionCode) {
        JobPost newPost = JobPost.create(
                memberRepository.findByUsername(username)
                        .orElseThrow(EntityNotFoundException.MemberNotFoundException::new),
                request.title(), request.location(),
                request.deadLine(), request.jobStartDate(), regionCode);

        return jobPostRepository.save(newPost);
    }

    private void createAndSaveJobPostDetail(JobPost newPost, String username, WriteJobPostRequest request) {
        JobPostDetail newPostDetail = JobPostDetail.create(newPost, username, request.body());

        jobPostdetailRepository.save(newPostDetail);
    }

    @Transactional
    public void modifyPost(String username, Long id, ModifyJobPostRequest request) {
        JobPostDetail postDetail = jobPostdetailRepository.findById(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
        JobPost jobPost = postDetail.getJobPost();

        validateModificationPermission(username, jobPost);

        int newRegionCode = Util.Region.getRegionCodeFromAddress(request.location());

        JobPostCategory existingTaskJobPostCategory = jobPostCategoryRepository.findByJobPostAndCategory_Type(jobPost, CategoryType.TASK);
        JobPostCategory existingRegionJobPostCategory = jobPostCategoryRepository.findByJobPostAndCategory_Type(jobPost, CategoryType.REGION);

        Category newTaskCategory = categoryRepository.findById(request.categoryId())
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);
        Category newRegionCategory = categoryRepository.findByName(RegionType.getNameByCode(newRegionCode))
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        jobPost.update(request.title(), request.deadLine(), request.jobStartDate(), request.location(), newRegionCode);

        existingTaskJobPostCategory.updateCategory(newTaskCategory);
        existingRegionJobPostCategory.updateCategory(newRegionCategory);

        updateJobPostDetails(postDetail, request);
        updateApplications(postDetail, request);
    }

    private void validateModificationPermission(String username, JobPost jobPost) {
        if (!canEditPost(username, jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private void updateJobPostDetails(JobPostDetail jobPostDetail, ModifyJobPostRequest request) {
        Wage existingWage = wageRepository.findByJobPostDetail(jobPostDetail);
        Essential existingEssential = essentialRepository.findByJobPostDetail(jobPostDetail);

        jobPostDetail.updatePostDetail(request.body());
        existingWage.updateWageInfo(request.cost(), request.payBasis(), request.workTime(), request.workDays());
        existingEssential.update(request.minAge(), request.gender());
    }

    private void updateApplications(JobPostDetail postDetail, ModifyJobPostRequest request) {
        List<JobApplication> applicationsToRemove = new ArrayList<>();
        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if (request.minAge() > LocalDateTime.now().plusYears(1).getYear() - jobApplication.getMember().getBirth().getYear()) {
                applicationsToRemove.add(jobApplication);
                publisher.publishEvent(new ChangeOfPostEvent(this, postDetail.getJobPost(), jobApplication, POST_MODIFICATION, DELETE));
            } else {
                publisher.publishEvent(new ChangeOfPostEvent(this, postDetail.getJobPost(), jobApplication, POST_MODIFICATION, NOTICE));
            }
        }

        postDetail.getJobApplications().removeAll(applicationsToRemove);
    }

    @Transactional
    public void deleteJobPost(String username, Long postId) {
        JobPost post = findByIdAndValidate(postId);
        List<JobPostImage> jobPostImages = post.getJobPostDetail().getJobPostImages();
        Member member = memberRepository.findByUsername(username)
                        .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        publisher.publishEvent(new PostDeletedEvent(this, post, member, DELETE));

        if (member.getRole() == Role.ADMIN || post.getMember().equals(member)) {
            if (!jobPostImages.isEmpty()) {
                imageStorage.deletePostImagesFromS3(jobPostImages);
            }

            jobPostCategoryRepository.deleteAllByJobPost(post);
            jobPostRepository.deleteById(postId);
        } else {
            throw new AuthException.NotAuthorizedException();
        }
    }

    public boolean canEditPost(String username, String author) {
        return username.equals(author);
    }

    public JobPost findByIdAndValidate(Long id) {
        return jobPostRepository.findById(id)
                .orElseThrow(EntityNotFoundException.PostNotExistsException::new);
    }

    public List<MyPostResponse> getMyJobPosts(String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException.MemberNotFoundException::new);

        return MyPostResponse.convertToList(jobPostRepository.findByMemberId(member.getId()));
    }

    public Page<JobPostBasicResponse> findByKw(List<String> kwTypes, String kw, String closed, String gender,
                                               int[] min_Age, List<String> location, int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));

        return JobPostBasicResponse.convertToPage(
                jobPostRepository.findByKw(kwTypes, kw, closed, gender, min_Age, location, pageable)
        );
    }

    @Cacheable(value = "jobPostsBySort", key = "#sortName + '_' + #pageable.pageNumber", condition = "#pageable.pageNumber < 5")
    public JobPostSortPageResponse findBySort(Pageable pageable) {

        return new JobPostSortPageResponse(
                new PageDto<>(
                        JobPostBasicResponse.convertToPage(jobPostRepository.findBySort(pageable))
                )
        );
    }

    public Pageable createPageableForSorting(int page, List<String> sortBys, List<String> sortOrders) {
        List<Sort.Order> sorts = new ArrayList<>();
        for (int i = 0; i < sortBys.size(); i++) {
            String sortBy = sortBys.get(i);
            String sortOrder = i < sortOrders.size() ? sortOrders.get(i) : "desc"; // 기본값은 desc
            sorts.add(new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy));
        }

        return PageRequest.of(page - 1, AppConfig.getBasePageSize(), Sort.by(sorts));
    }

    public List<InterestedJobPostResponse> findByInterestAndUsername(Long memberId) {
        return InterestedJobPostResponse.convertToList(
                jobPostdetailRepository.findByInterestsMemberId(memberId)
        );

    }

    @Cacheable(value = "jobPostsBySearch", key = "#titleAndBody + '_' + #titleOnly + '_' + #bodyOnly")
    public List<JobPostBasicResponse> searchJobPostsByTitleAndBody(String titleAndBody, String titleOnly, String bodyOnly) {
        Specification<JobPost> spec = Specification.where(null);

        spec = applyTitleAndBodySearch(spec, titleAndBody);
        spec = applyTitleOnlySearch(spec, titleOnly);
        spec = applyBodyOnlySearch(spec, bodyOnly);

        return JobPostBasicResponse.convertToList(jobPostRepository.findAll(spec));
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

    public List<JobPost> findExpiredJobPosts(LocalDate currentDate) { //    ver.  LocalDate
        return jobPostRepository.findByClosedFalseAndDeadlineBefore(currentDate);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void checkAndCloseExpiredJobPosts() {
        List<JobPost> expiredJobPosts = findExpiredJobPosts(LocalDate.now());
        for (JobPost jobPost : expiredJobPosts) {
            publisher.publishEvent(new PostDeadlineEvent(this, jobPost));
        }
    }

    @Transactional
    public void closeJobPostEarly(String username, Long id) {
        JobPost jobPost = findByIdAndValidate(id);

        if (!canEditPost(username, jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }

        jobPost.setDeadlineNull();
        publisher.publishEvent(new PostDeadlineEvent(this, jobPost));
    }

    @Cacheable(value = "jobPostsByCategory", key = "#categoryId + '_' + #page", condition = "#page <= 5")
    public Page<JobPostBasicResponse> getPostsByCategory(Long categoryId, int page) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(EntityNotFoundException.NotFoundCategoryException::new);

        Pageable pageable = createPageableForCategory(page);
        Page<JobPost> jobPosts = jobPostCategoryRepository.findJobPostsByCategoryId(category.getId(), pageable);

        return JobPostBasicResponse.convertToPage(jobPosts);
    }

    private Pageable createPageableForCategory(int page) {
        return PageRequest.of(page - 1, 10); // Convert page number to zero-based index
    }
}
