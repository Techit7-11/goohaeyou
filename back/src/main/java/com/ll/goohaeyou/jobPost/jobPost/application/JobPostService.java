package com.ll.goohaeyou.jobPost.jobPost.application;

import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.category.domain.Category;
import com.ll.goohaeyou.category.domain.repository.CategoryRepository;
import com.ll.goohaeyou.category.domain.repository.JobPostCategoryRepository;
import com.ll.goohaeyou.category.application.CategoryService;
import com.ll.goohaeyou.category.application.JobPostCategoryService;
import com.ll.goohaeyou.global.standard.base.util.Util;
import com.ll.goohaeyou.image.application.S3ImageService;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostDetailDto;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostDto;
import com.ll.goohaeyou.jobPost.jobPost.application.dto.JobPostForm;
import com.ll.goohaeyou.member.member.domain.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.member.member.domain.type.Role;
import com.ll.goohaeyou.member.member.application.MemberService;
import com.ll.goohaeyou.global.event.notification.*;
import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.category.exception.CategoryException;
import com.ll.goohaeyou.jobPost.jobPost.exception.JobPostException;
import com.ll.goohaeyou.member.member.exception.MemberException;
import com.ll.goohaeyou.global.standard.base.RegionType;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPost;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostDetail;
import com.ll.goohaeyou.jobPost.jobPost.domain.JobPostImage;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostDetailRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostRepository;
import com.ll.goohaeyou.jobPost.jobPost.domain.repository.JobPostSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

import static com.ll.goohaeyou.notification.domain.type.CauseTypeCode.POST_MODIFICATION;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.DELETE;
import static com.ll.goohaeyou.notification.domain.type.ResultTypeCode.NOTICE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostDetailRepository jobPostdetailRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final S3ImageService s3ImageService;
    private final CategoryRepository categoryRepository;
    private final JobPostCategoryRepository jobPostCategoryRepository;
    private final JobPostCategoryService jobPostCategoryService;
    private final CategoryService categoryService;
    private final EssentialService essentialService;
    private final WageService wageService;

    @Transactional
    public void writePost(String username, JobPostForm.Register form) {
        int regionCode = Util.Region.getRegionCodeFromAddress(form.getLocation());
        Category taskCategory = categoryService.getCategoryById(form.getCategoryId());
        Category regionCategory = categoryService.getCategoryByName(RegionType.getNameByCode(regionCode));

        JobPost newPost = createAndSaveJobPost(username, form, regionCode);

        jobPostCategoryService.createAndSaveJobPostCategory(newPost, taskCategory);
        jobPostCategoryService.createAndSaveJobPostCategory(newPost, regionCategory);

        JobPostDetail postDetail = createAndSaveJobPostDetail(newPost, username, form);

        essentialService.createAndSaveEssential(postDetail, form);

        wageService.createAndSaveWage(postDetail, form);
    }

    public JobPostDetailDto findById(Long id) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(id);

        return JobPostDetailDto.from(postDetail.getJobPost(), postDetail, postDetail.getEssential(), postDetail.getWage());
    }

    public List<JobPostDto> findAll() {
        return JobPostDto.convertToDtoList(jobPostRepository.findAll());
    }

    private JobPost createAndSaveJobPost(String username, JobPostForm.Register form, int regionCode) {
        JobPost newPost = JobPost.create(memberService.getMember(username), form.getTitle(), form.getLocation(),
                form.getDeadLine(), form.getJobStartDate(), regionCode);

        return jobPostRepository.save(newPost);
    }

    private JobPostDetail createAndSaveJobPostDetail(JobPost newPost, String username, JobPostForm.Register form) {
        JobPostDetail newPostDetail = JobPostDetail.create(newPost, username, form.getBody());

        return jobPostdetailRepository.save(newPostDetail);
    }

    @Transactional
    public void modifyPost(String username, Long id, JobPostForm.Modify form) {
        JobPostDetail postDetail = findByJobPostAndNameAndValidate(id);
        JobPost jobPost = postDetail.getJobPost();

        validateModificationPermission(username, jobPost);

        int newRegionCode = Util.Region.getRegionCodeFromAddress(form.getLocation());

        Category newTaskCategory = categoryService.getCategoryById(form.getCategoryId());
        Category newRegionCategory = categoryService.getCategoryByName(RegionType.getNameByCode(newRegionCode));

        jobPost.update(form.getTitle(), form.getDeadLine(), form.getJobStartDate(), form.getLocation(), newRegionCode);
        jobPostCategoryService.updateJobPostCategories(jobPost, newTaskCategory, newRegionCategory);
        updateJobPostDetails(postDetail, form, newRegionCode);
        updateApplications(postDetail, form);
    }

    private void validateModificationPermission(String username, JobPost jobPost) {
        if (!canEditPost(username, jobPost.getMember().getUsername())) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    private void updateJobPostDetails(JobPostDetail jobPostDetail, JobPostForm.Modify form, int newRegionCode) {
        jobPostDetail.updatePostDetail(form.getBody());
        essentialService.updateEssential(jobPostDetail.getEssential(), form);
        wageService.updateWage(jobPostDetail.getWage(), form);
    }

    private void updateApplications(JobPostDetail postDetail, JobPostForm.Modify form) {
        List<JobApplication> applicationsToRemove = new ArrayList<>();
        for (JobApplication jobApplication : postDetail.getJobApplications()) {
            if (form.getMinAge() > LocalDateTime.now().plusYears(1).getYear() - jobApplication.getMember().getBirth().getYear()) {
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

    public List<JobPostDto> findByUsername(String username) {

        Member member = memberService.getMember(username);

        return JobPostDto.convertToDtoList(jobPostRepository.findByMemberId(member.getId()));
    }

    public Page<JobPost> findByKw(List<String> kwTypes, String kw, String closed, String gender, int[] min_Age,
                                  List<String> location, Pageable pageable) {
        return jobPostRepository.findByKw(kwTypes, kw, closed, gender, min_Age, location, pageable);
    }

    @Cacheable(value = "jobPostsBySort", key = "#sortName + '_' + #pageable.pageNumber", condition = "#pageable.pageNumber < 5")
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

    @Cacheable(value = "jobPostsBySearch", key = "#titleAndBody + '_' + #titleOnly + '_' + #bodyOnly")
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

    @Cacheable(value = "jobPostsByCategory", key = "#categoryName + '_' + #pageable.pageNumber", condition = "#pageable.pageNumber < 5")
    public Page<JobPostDto> getPostsByCategory(String categoryName, Pageable pageable) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(CategoryException.NotFoundCategoryException::new);

        Page<JobPost> jobPosts = jobPostCategoryRepository.findJobPostsByCategoryId(category.getId(), pageable);

        return JobPostDto.convertToDtoPage(jobPosts);
    }
}
