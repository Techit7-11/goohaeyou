package com.ll.goohaeyou.jobPost.jobPost.infrastructure;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.ll.goohaeyou.global.standard.base.util.MimeTypeUtil;
import com.ll.goohaeyou.image.exception.ImageException;
import com.ll.goohaeyou.jobApplication.domain.ImageStorage;
import com.ll.goohaeyou.jobPost.jobPost.domain.entity.JobPostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3ImageService implements ImageStorage {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    // 이미지 업로드 (S3에 저장된 이미지 객체의 public url 반환)
    @Transactional
    @Override
    public String upload(MultipartFile image) {
        if (Objects.isNull(image) || image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new ImageException.FileIsEmptyException();
        }
        return uploadImage(image);
    }

    // 이미지 업로드 수행
    private String uploadImage(MultipartFile image) {
        validateImageFileExtension(Objects.requireNonNull(image.getOriginalFilename()));   // 이미지 확장자 유효성 검사
        try {
            return uploadImageToS3(image);   // AWS S3에 이미지 업로드
        } catch (IOException e) {
            throw new ImageException.IOExceptionOnImageDeleteException();
        }
    }

    // 이미지 파일 확장자 유효성 검사
    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {   // 파일명에 점이 없으면 예외 발생
            throw new ImageException.NoFileExtensionException();
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();   // 확장자를 추출하여 소문자로 변환
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensionList.contains(extension)) {   // 허용된 확장자 목록에 속하지 않으면 예외 발생
            throw new ImageException.InvalidFileExtensionException();
        }
    }

    // AWS S3에 이미지 업로드
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();   // 원본 파일명
        String extension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".") + 1);  // 확장자 추출시 시작점이 '.' 이므로, +1을 해주어야 확장자만 추출된다.

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;  // S3에 저장될 파일명

        InputStream inputStream = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);  // 이미지 데이터를 바이트 배열로 변환

        ObjectMetadata metadata = new ObjectMetadata();
        MimeTypeUtil.getMimeType(extension).ifPresentOrElse(
            metadata::setContentType,   // 메타데이터 설정 ex) image/png
            () -> { throw new ImageException.InvalidFileExtensionException(); }
        );
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata);
            amazonS3.putObject(putObjectRequest);
        } catch (AmazonServiceException e) {
            throw new ImageException.AWSServiceException();
        } catch (SdkClientException e) {
            throw new ImageException.AWSClientException();
        } finally {
            byteArrayInputStream.close();
            inputStream.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();   // 업로드된 이미지의 URL 반환
    }

    // AWS S3에서 이미지 삭제
    @Transactional
    @Override
    public void deleteImageFromS3(String imageAddress) {
        String fileName = getFileNameFromImageAddress(imageAddress);   // 이미지 주소에서 파일명 추출
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));   // S3에서 이미지 삭제 요청
        } catch (Exception e) {
            throw new ImageException.IOExceptionOnImageDeleteException();
        }
    }

    @Transactional
    @Override
    public void deletePostImagesFromS3(List<JobPostImage> jobPostImages) {
        if (jobPostImages.isEmpty()) {
            throw new ImageException.PostImagesNotFoundException();
        }

        for (JobPostImage jobPostImage : jobPostImages) {
            String fileName = getFileNameFromImageAddress(jobPostImage.getJobPostImageUrl());   // 이미지 주소에서 파일명 추출
            try {
                amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));   // S3에서 이미지 삭제 요청
            } catch (Exception e) {
                throw new ImageException.IOExceptionOnImageDeleteException();
            }
        }
    }

    // 이미지 주소로부터 파일명을 추출
    private String getFileNameFromImageAddress(String imageAddress) {
        try {
            URI uri = new URI(imageAddress);
            String decodingKey = URLDecoder.decode(uri.getPath(), StandardCharsets.UTF_8);
            return decodingKey.substring(1);  // 맨 앞의 '/' 제거
        } catch (URISyntaxException e) {
            throw new ImageException.IOExceptionOnImageDeleteException();
        }
    }
}
