package com.ll.gooHaeYu.global.exception;

import com.ll.gooHaeYu.global.rsData.RsData;
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
    public RsData<List<String>> handleValidException(MethodArgumentNotValidException e) {

        List<String> errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();

        log.error(errorMessages.toString());

        return RsData.of(
                "400",
                "VALIDATION_EXCEPTION",
                errorMessages
        );
    }

    @ExceptionHandler(CustomException.class)
    public RsData<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .build();

        log.error(e.getMessage(), e);

        return RsData.of(
                Integer.toString(e.getErrorCode().getStatus().value()),
                "CUSTOM_EXCEPTION",
                errorResponse
        );
    }
}
