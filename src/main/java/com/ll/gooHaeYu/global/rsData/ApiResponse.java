package com.ll.gooHaeYu.global.rsData;

import com.ll.gooHaeYu.global.exception.CustomHttpStatus;
import com.ll.gooHaeYu.global.exception.ErrorCode;
import com.ll.gooHaeYu.global.exception.type.ApiResultType;
import com.ll.gooHaeYu.standard.base.Empty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Getter
public class ApiResponse<T> {
    private final int statusCode;
    @NotNull
    private final String message;
    @NotNull
    private final ApiResultType resultType;
    @NotNull
    private final T data;

    private ApiResponse(int statusCode, String message, ApiResultType resultType, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.resultType = resultType;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase(),
                ApiResultType.SUCCESS,
                data
        );
    }

    public static ApiResponse<Empty> created() {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.getReasonPhrase(),
                ApiResultType.SUCCESS,
                new Empty()
        );
    }

    public static ApiResponse<URI> created(String uri) {
        URI newUri = URI.create(uri);
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.getReasonPhrase(),
                ApiResultType.SUCCESS,
                newUri
        );
    }

    public static ApiResponse<Empty> noContent() {
        return new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.getReasonPhrase(),
                ApiResultType.SUCCESS,
                new Empty()
        );
    }

    public static ApiResponse<Empty> validationException(CustomHttpStatus customHttpStatus) {
        return new ApiResponse<>(
                customHttpStatus.getStatusCode(),
                customHttpStatus.getStatusMessage(),
                ApiResultType.VALIDATION_EXCEPTION,
                new Empty()
        );
    }

    public static ApiResponse<Empty> customException(ErrorCode errorCode) {
        return new ApiResponse<>(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                ApiResultType.CUSTOM_EXCEPTION,
                new Empty()
        );
    }
}
