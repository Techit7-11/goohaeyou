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

    REVIEW_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 리뷰는 존재하지 않습니다."),

    REVIEW_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "이미 리뷰를 작성하셨습니다."),

    COMMENT_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 댓글은 존재하지 않습니다."),

    NOTIFICATION_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 알림은 존재하지 않습니다."),

    // NOT_EDITABLE -> NOT_ABLE 더 넓은 범위에 사용하기 위해 변경
    NOT_ABLE(HttpStatus.FORBIDDEN,"권한이 없습니다."),

    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 token 입니다."),

    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 로그인 요청입니다."),

    CANNOT_SUBMISSION(HttpStatus.CONFLICT,"지원 불가능 합니다."),

    CLOSED_POST(HttpStatus.BAD_REQUEST, "마감된 공고입니다."),

    DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "중복 지원은 불가능합니다."),

    UNSATISFIED_REQUIREMENTS(HttpStatus.FORBIDDEN, "지원 필수요건에 충족되지 못합니다."),

    NOT_ELIGIBLE_FOR_OWN_JOB(HttpStatus.FORBIDDEN, "본인의 공고에는 지원이 불가능합니다."),

    NOT_POSSIBLE_TO_APPROVE_IT_YET(HttpStatus.FORBIDDEN,"공고 마감 후 승인이 가능 합니다.");;

    private final HttpStatus status;
    private final String message;
}
