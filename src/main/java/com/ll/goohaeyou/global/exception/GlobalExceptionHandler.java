package com.ll.goohaeyou.global.exception;

import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.standard.base.Empty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Empty> handleValidException(MethodArgumentNotValidException e) {

        List<String> errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();

        log.error(errorMessages.toString());    // 모든 유효성 검사 에러를 로그로 찍음

        CustomHttpStatus customHttpStatus = CustomHttpStatus.builder()
                .statusCode(400)
                .statusMessage(errorMessages.get(0))   // 클라이언트에게는 첫 번째 에러를 반환
                .build();

        return ApiResponse.validationException(customHttpStatus);
    }

    @ExceptionHandler(CustomException.class)
    public ApiResponse<Empty> handleCustomException(CustomException customException) {

        log.error(customException.getErrorCode() + ": " + customException.getMessage());

        return ApiResponse.customException(customException.getErrorCode());
    }
}
