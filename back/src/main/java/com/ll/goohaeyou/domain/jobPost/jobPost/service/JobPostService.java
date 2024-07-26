package com.ll.goohaeyou.domain.jobPost.jobPost.service;

import com.ll.goohaeyou.domain.application.entity.Application;
import com.ll.goohaeyou.domain.category.entity.Category;
import com.ll.goohaeyou.domain.category.entity.JobPostCategory;
import com.ll.goohaeyou.domain.category.entity.repository.CategoryRepository;
import com.ll.goohaeyou.domain.category.entity.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.domain.category.entity.type.CategoryType;
import com.ll.goohaeyou.domain.category.service.JobPostCategoryService;
import com.ll.goohaeyou.domain.fileupload.service.S3ImageService;
import com.ll.goohaeyou.domain.jobPost.jobPost.dto.JobPostDetailDto;
import com.ll.goohaeyou.domain.jobPost.jobPost.dto.JobPostDto;
import com.ll.goohaeyou.domain.jobPost.jobPost.dto.JobPostForm;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.*;
import com.ll.goohaeyou.domain.jobPost.jobPost.entity.repository.*;
import com.ll.goohaeyou.domain.member.member.entity.Member;
import com.ll.goohaeyou.domain.member.member.entity.repository.MemberRepository;
import com.ll.goohaeyou.domain.member.member.entity.type.Role;
import com.ll.goohaeyou.domain.member.member.service.MemberService;
import com.ll.goohaeyou.global.event.notification.*;
import com.ll.goohaeyou.global.exception.auth.AuthException;
import com.ll.goohaeyou.global.exception.category.CategoryException;
import com.ll.goohaeyou.global.exception.jobPost.JobPostException;
import com.ll.goohaeyou.global.exception.member.MemberException;
import com.ll.goohaeyou.global.standard.base.RegionType;
import com.ll.goohaeyou.global.standard.base.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ll.goohaeyou.domain.notification.entity.type.CauseTypeCode.POST_MODIFICATION;
import static com.ll.goohaeyou.domain.notification.entity.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.domain.notification.entity.type.ResultTypeCode.NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostDetailRepository jobPostdetailRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final EssentialRepository essentialRepository;
    private final ApplicationEventPublisher publisher;
    private final WageRepository wageRepository;
    private final S3ImageService s3ImageService;
    private final CategoryRepository categoryRepository;
    private final JobPostCategoryRepository jobPostCategoryRepository;
    private final JobPostCategoryService jobPostCategoryService;

    @Transactional
    public JobPostForm.Register writePost(String username, JobPostForm.Register form) {
        // 지역 코드 및 카테고리 찾기
        int regionCode = Ut.Region.getRegionCodeFromAddress(form.getLocation());
        Category taskCategory = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(CategoryException.NotFoundCategoryException::new);
        Category regionCategory = categoryRepository.findByName(RegionType.getNameByCode(regionCode))
                .orElseThrow(CategoryException.NotFoundCategoryException::new);

        JobPost newPost = createAndSaveJobPost(username, form, regionCode);

        createAndSaveJobPostCategory(newPost, taskCategory);
        createAndSaveJobPostCategory(newPost, regionCategory);

        JobPostDetail postDetail = createAndSaveJobPostDetail(newPost, username, form);

        createAndSaveEssential(postDetail, form);

        createAndSaveWage(postDetail, form);

        return form;
    }

    public JobPostDetailDto findById(Long id) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(id);

        return JobPostDetailDto.from(postDetail.getJobPost(), postDetail, postDetail.getEssential(), postDetail.getWage());
    }

    public List<JobPostDto> findAll() {
        return JobPostDto.convertToDtoList(jobPostRepository.findAll());
    }

    private JobPost createAndSaveJobPost(String username, JobPostForm.Register form, int regionCode) {
        JobPost newPost = JobPost.builder()
                .member(memberService.getMember(username))
                .title(form.getTitle())
                .location(form.getLocation())
                .deadline(form.getDeadLine())
                .jobStartDate(form.getJobStartDate())
                .regionCode(regionCode)
                .build();

        return jobPostRepository.save(newPost);
    }

    private void createAndSaveJobPostCategory(JobPost newPost, Category category) {
        JobPostCategory jobPostCategory = JobPostCategory.builder()
                .jobPost(newPost)
                .category(category)
                .build();

        jobPostCategoryRepository.save(jobPostCategory);
    }

    private JobPostDetail createAndSaveJobPostDetail(JobPost newPost, String username, JobPostForm.Register form) {
        JobPostDetail postDetail = JobPostDetail.builder()
                .jobPost(newPost)
                .author(username)
                .body(form.getBody())
                .build();

        return jobPostdetailRepository.save(postDetail);
    }

    private void createAndSaveEssential(JobPostDetail postDetail, JobPostForm.Register form) {
        Essential essential = Essential.builder()
                .minAge(form.getMinAge())
                .gender(form.getGender())
                .jobPostDetail(postDetail)
                .build();

        essentialRepository.save(essential);
    }

    private void createAndSaveWage(JobPostDetail postDetail, JobPostForm.Register form) {
        Wage wage = Wage.builder()
                .cost(form.getCost())
                .workTime(form.getWorkTime())
                .workDays(form.getWorkDays())
                .payBasis(form.getPayBasis())
                .wagePaymentMethod(form.getWagePaymentMethod())
                .jobPostDetail(postDetail)
                .build();

        wageRepository.save(wage);
    }

    @Transactional
    public JobPostForm.Modify modifyPost(String username, Long id, JobPostForm.Modify form) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(id);
        JobPost jobPost = postDetail.getJobPost();

        validateModificationPermission(username, jobPost);

        int newRegionCode = Ut.Region.getRegionCodeFromAddress(form.getLocation());

        Category newTaskCategory = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(CategoryException.NotFoundCategoryException::new);
        Category newRegionCategory = categoryRepository.findByName(RegionType.getNameByCode(newRegionCode))
                .orElseThrow(CategoryException.NotFoundCategoryException::new);

        jobPost.update(form.getTitle(), form.getDeadLine(), form.getJobStartDate(), form.getLocation(), newRegionCode);
        updateJobPostCategories(jobPost, newTaskCategory, newRegionCategory);
        updateJobPostDetails(postDetail, form, newRegionCode);
        updateApplications(postDetail, form);

        return form;
    }

    private void validateModificationPermission(String username, JobPost jobPost) {
        if (!canEditPost(username, jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private void updateJobPostCategories(JobPost jobPost, Category newTaskCategory, Category newRegionCategory) {
        JobPostCategory taskJobPostCategory = jobPostCategoryService.findByJobPostAndCategoryType(jobPost, CategoryType.TASK);
        JobPostCategory regionJobPostCategory = jobPostCategoryService.findByJobPostAndCategoryType(jobPost, CategoryType.REGION);

        taskJobPostCategory.updateCategory(newTaskCategory);
        regionJobPostCategory.updateCategory(newRegionCategory);
    }

    private void updateJobPostDetails(JobPostDetail jobPostDetail, JobPostForm.Modify form, int newRegionCode) {
        jobPostDetail.updatePostDetail(form.getBody());
        jobPostDetail.getEssential().update(form.getMinAge(), form.getGender());
        jobPostDetail.getWage().updateWageInfo(form.getCost(), form.getPayBasis(), form.getWorkTime(), form.getWorkDays());
    }

    private void updateApplications(JobPostDetail postDetail, JobPostForm.Modify form) {
        List<Application> applicationsToRemove = new ArrayList<>();
        for (Application application : postDetail.getApplications()) {
            if (form.getMinAge() > LocalDateTime.now().plusYears(1).getYear() - application.getMember().getBirth().getYear()) {
                applicationsToRemove.add(application);
                publisher.publishEvent(new ChangeOfPostEvent(this, postDetail.getJobPost(), application, POST_MODIFICATION, DELETE));
            } else {
                publisher.publishEvent(new ChangeOfPostEvent(this, postDetail.getJobPost(), application, POST_MODIFICATION, NOTICE));
            }
        }

        postDetail.getApplications().removeAll(applicationsToRemove);
    }

    @Transactional
    public void deleteJobPost(String username, Long postId) {
        JobPost post = findByIdAndValidate(postId);
        List<JobPostImage> jobPostImages = post.getJobPostDetail().getJobPostImages();
        Member member = findUserByUserNameValidate(username);

        publisher.publishEvent(new PostDeletedEvent(this, post, member, DELETE));

        if (member.getRole() == Role.ADMIN || post.getMember().equals(member)) {
            if (!jobPostImages.isEmpty()) {
                s3ImageService.deletePostImagesFromS3(jobPostImages);
            }

            jobPostCategoryRepository.deleteAllByJobPost(post);
            jobPostRepository.deleteById(postId);
        } else {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private Member findUserByUserNameValidate(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberException.MemberNotFoundException::new);
    }


    public boolean canEditPost(String username, String author) {
        return username.equals(author);
    }

    public JobPost findByIdAndValidate(Long id) {
        return jobPostRepository.findById(id)
                .orElseThrow(JobPostException.PostNotExistsException::new);
    }

    public JobPostDetail findByJobPostAndNameAndValidate(Long postId) {
        JobPost post = findByIdAndValidate(postId);

        return jobPostdetailRepository.findByJobPostAndAuthor(post, post.getMember().getUsername())
                .orElseThrow(JobPostException.PostNotExistsException::new);
    }

    @Transactional
    public void interest(String username, Long postId) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(postId);
        Member member = memberService.getMember(username);

        if (hasInterest(postDetail, member)) {
            throw new AuthException.NotAuthorizedException();
        }

        postDetail.getInterests().add(Interest.builder()
                .jobPostDetail(postDetail)
                .member(member)
                .build());

        postDetail.getJobPost().increaseInterestCount();

        if (!postDetail.getAuthor().equals(username)) {
            publisher.publishEvent(new PostGetInterestedEvent(this, postDetail, member));
        }
    }

    @Transactional
    public void disinterest(String username, Long postId) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(postId);
        Member member = memberService.getMember(username);

        if (!hasInterest(postDetail, member)) {
            throw new AuthException.NotAuthorizedException();
        }

        postDetail.getInterests().removeIf(interest -> interest.getMember().equals(member));
        postDetail.getJobPost().decreaseInterestCount();
    }

    public boolean hasInterest(JobPostDetail post, Member member) {
        return post.getInterests().stream()
                .anyMatch(interest -> interest.getMember().equals(member));
    }

    public boolean isInterested(String username, Long id) {
        List<String> interestedUsernames = findById(id).getInterestedUsernames();

        return interestedUsernames.stream()
                .anyMatch(username::equals);
    }

    public List<JobPostDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return JobPostDto.convertToDtoList(jobPostRepository.findByMemberId(member.getId()));
    }

    public Page<JobPost> findByKw(List<String> kwTypes, String kw, String closed, String gender, int[] min_Age, List<String> location, Pageable pageable) {
        return jobPostRepository.findByKw(kwTypes, kw, closed, gender, min_Age, location, pageable);
    }

    public Page<JobPost> findBySort(Pageable pageable) {
        return jobPostRepository.findBySort(pageable);
    }

    public List<JobPostDto> findByInterestAndUsername(Long memberId) {
        return jobPostdetailRepository.findByInterestsMemberId(memberId)
                .stream()
                .map(JobPostDetail::getJobPost)
                .map(JobPostDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void increaseViewCount(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(JobPostException.PostNotExistsException::new);

        jobPost.increaseViewCount();
    }

    public List<JobPostDto> searchJobPostsByTitleAndBody(String titleAndBody, String titleOnly, String bodyOnly) {
        Specification<JobPost> spec = Specification.where(null);

        spec = applyTitleAndBodySearch(spec, titleAndBody);
        spec = applyTitleOnlySearch(spec, titleOnly);
        spec = applyBodyOnlySearch(spec, bodyOnly);

        return jobPostRepository.findAll(spec)
                .stream()
                .map(JobPostDto::from)
                .collect(Collectors.toList());
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

    @EventListener
    @Transactional
    public void jobPostClosedEventListen(PostDeadlineEvent event) {
        JobPost jobPost = event.getJobPost();
        jobPost.close();
        jobPostRepository.save(jobPost);
    }

    @EventListener
    @Transactional
    public void jobPostEmployedEventListen(PostEmployedEvent event) {
        JobPost jobPost = event.getJobPost();
        jobPost.employed();
        jobPostRepository.save(jobPost);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")    // 00:00:00.000000에 실행
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

    public Page<JobPostDto> getPostsByCategory(String categoryName, Pageable pageable) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryException.NotFoundCategoryException::new);

        Page<JobPost> jobPosts = jobPostCategoryRepository.findJobPostsByCategoryId(category.getId(), pageable);

        return JobPostDto.convertToDtoPage(jobPosts);
    }
}