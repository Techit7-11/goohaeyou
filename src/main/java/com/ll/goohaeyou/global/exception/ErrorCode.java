package com.ll.goohaeyou.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."), // m

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 회원정보가 없습니다."), // m

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "패스워드를 잘못 입력하셨습니다."),  // a

    POST_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 공고는 존재하지 않습니다."),  // j-j

    REVIEW_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당 리뷰는 존재하지 않습니다."),  // r

    REVIEW_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "이미 리뷰를 작성하셨습니다."),  // r

    COMMENT_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당 댓글은 존재하지 않습니다."),  // j-c

    NOTIFICATION_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당 알림은 존재하지 않습니다."), // n

    NOT_AUTHORIZED(HttpStatus.FORBIDDEN,"권한이 없습니다."),  // a

    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 로그인 요청입니다."),  // a

    CLOSED_POST(HttpStatus.BAD_REQUEST, "마감된 공고입니다."),  // ap

    DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "중복 지원은 불가능합니다."),  // ap

    NOT_ELIGIBLE_FOR_OWN_JOB(HttpStatus.FORBIDDEN, "본인의 공고에는 지원이 불가능합니다."),  // ap

    NOT_POSSIBLE_TO_APPROVE_IT_YET(HttpStatus.FORBIDDEN,"공고 마감 후 승인이 가능 합니다."),  // j-e

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 결제건 입니다."),  // p

    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),  // p

    NO_ENUM_CONSTANT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 타입에 해당하는 enum 상수가 없습니다."), // p

    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 결제건 입니다."),  // p

    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),  // p

    CHATROOM_NOT_EXISTS(HttpStatus.NOT_FOUND,"해당 채팅방은 존재하지 않습니다."),  // c

    MESSAGE_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당 메세지는 존재하지 않습니다."),  // c

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),  // bad

    COMPLETION_NOT_POSSIBLE(HttpStatus.BAD_REQUEST, "현재 상태 에서는 완료 처리가 불가능 합니다."), // j-e

    INVALID_WAGE_PAYMENT_METHOD(HttpStatus.NOT_FOUND, "유효하지 않은 방법 입니다."), // j-e

    PAYMENT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보가 없습니다."), // p

    PAYMENT_REQUEST_CONFLICT(HttpStatus.CONFLICT, "현재 다른 결제 요청을 처리 중입니다. 잠시 후 다시 시도해 주세요."), // p

    EMAIL_ALREADY_AUTHENTICATED(HttpStatus.BAD_REQUEST, "이미 이메일 인증이 완료된 상태입니다."), // m-e

    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "잘못된 인증코드 입니다."),  // m-e

    INITIATE_EMAIL_REQUEST(HttpStatus.BAD_REQUEST, "먼저 이메일 인증 요청을 진행해주세요."), // m-e

    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),  // i

    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다."),  // i

    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자 입니다."),  // i

    AWS_SERVICE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "AWS 서비스 오류가 발생했습니다."), // i

    AWS_CLIENT_EXCEPTION(HttpStatus.BAD_REQUEST, "AWS 클라이언트 요청이 잘못되었습니다."),  // i

    IO_EXCEPTION_ON_IMAGE_DELETE(HttpStatus.BAD_REQUEST, "이미지를 삭제하는 중 입출력 오류가 발생했습니다."), // i

    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 이미지가 존재하지 않습니다."),  // i

    POST_IMAGES_NOT_FOUND(HttpStatus.NOT_FOUND, "공고에 등록된 이미지가 없습니다."),  // i

    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지가 존재하지 않습니다."),  // i

    INVALID_ADDRESS_FORMAT(HttpStatus.NOT_FOUND, "올바른 주소 형식이 아닙니다."),

    INVALID_CATEGORY_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 카테고리 형식입니다."),  // i

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다.");  // i

    private final HttpStatus status;
    private final String message;
}
