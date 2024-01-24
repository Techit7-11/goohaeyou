package com.ll.gooHaeYu.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "중복된 ID입니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 회원정보가 없습니다."),

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "패스워드를 잘못 입력하셨습니다."),

    POST_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 공고는 존재하지 않습니다."),

    NOT_EDITABLE(HttpStatus.FORBIDDEN,"수정 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
