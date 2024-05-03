package com.ll.gooHaeYu.domain.fileupload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.ll.gooHaeYu.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.ll.gooHaeYu.global.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    // 이미지 업로드 (S3에 저장된 이미지 객체의 public url 반환)
    public String upload(MultipartFile image) {
        // 이미지가 비어 있거나 파일명이 null이면 예외 발생
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new CustomException(FILE_IS_EMPTY);
        }
        return uploadImage(image);   // 이미지 업로드 메서드 호출
    }

    // 이미지 업로드 수행
    private String uploadImage(MultipartFile image) {
        validateImageFileExtension(image.getOriginalFilename());   // 이미지 확장자 유효성 검사
        try {
            return uploadImageToS3(image);   // AWS S3에 이미지 업로드
        } catch (IOException e) {
            throw new CustomException(IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    // 이미지 파일 확장자 유효성 검사
    private void validateImageFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {   // 파일명에 점이 없으면 예외 발생
            throw new CustomException(NO_FILE_EXTENSION);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();   // 확장자를 추출하여 소문자로 변환
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtensionList.contains(extension)) {   // 허용된 확장자 목록에 속하지 않으면 예외 발생
            throw new CustomException(INVALID_FILE_EXTENSION);
        }
    }

    // AWS S3에 이미지 업로드
    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();   // 원본 파일명
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));  // 확장자

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename;  // S3에 저장될 파일명

        InputStream inputStream = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);  // 이미지 데이터를 바이트 배열로 변환

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extension);  // 메타데이터 설정
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);  // S3에 이미지 업로드 요청
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new CustomException(PUT_OBJECT_EXCEPTION);
        } finally {
            byteArrayInputStream.close();
            inputStream.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();   // 업로드된 이미지의 URL 반환
    }

    // AWS S3에서 이미지 삭제
    public void deleteImageFromS3(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);   // 이미지 주소에서 파일명 추출
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));   // S3에서 이미지 삭제 요청
        } catch (Exception e) {
            throw new CustomException(IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    // 이미지 주소로부터 파일명을 추출
    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);  // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new CustomException(IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }
}
