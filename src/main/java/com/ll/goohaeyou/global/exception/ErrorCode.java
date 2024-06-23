package com.ll.goohaeyou.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 회원정보가 없습니다."),

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "패스워드를 잘못 입력하셨습니다."),

    POST_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 공고는 존재하지 않습니다."),

    REVIEW_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 리뷰는 존재하지 않습니다."),

    REVIEW_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "이미 리뷰를 작성하셨습니다."),

    COMMENT_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 댓글은 존재하지 않습니다."),

    NOTIFICATION_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 알림은 존재하지 않습니다."),

    NOT_ABLE(HttpStatus.FORBIDDEN,"권한이 없습니다."),

    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 token 입니다."),

    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 로그인 요청입니다."),

    CANNOT_SUBMISSION(HttpStatus.CONFLICT,"지원 불가능 합니다."),

    CLOSED_POST(HttpStatus.BAD_REQUEST, "마감된 공고입니다."),

    DUPLICATE_SUBMISSION(HttpStatus.CONFLICT, "중복 지원은 불가능합니다."),

    UNSATISFIED_REQUIREMENTS(HttpStatus.FORBIDDEN, "지원 필수요건에 충족되지 못합니다."),

    NOT_ELIGIBLE_FOR_OWN_JOB(HttpStatus.FORBIDDEN, "본인의 공고에는 지원이 불가능합니다."),

    NOT_POSSIBLE_TO_APPROVE_IT_YET(HttpStatus.FORBIDDEN,"공고 마감 후 승인이 가능 합니다."),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 결제건 입니다."),

    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),

    ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "이미 승인된 결제건 입니다."),

    NO_ENUM_CONSTANT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 타입에 해당하는 enum 상수가 없습니다."),

    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 결제건 입니다."),

    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),

    CHATROOM_NOT_EXITS(HttpStatus.NOT_FOUND,"해당 채팅방은 존재하지 않습니다."),

    MESSAGE_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 메세지는 존재하지 않습니다."),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    COMPLETION_NOT_POSSIBLE(HttpStatus.BAD_REQUEST, "현재 상태 에서는 완료 처리가 불가능 합니다."),

    INVALID_WAGE_PAYMENT_METHOD(HttpStatus.NOT_FOUND, "유효하지 않은 방법 입니다."),

    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "금액이 일치하지 않습니다."),

    PAYMENT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보가 없습니다."),

    PAYMENT_REQUEST_CONFLICT(HttpStatus.CONFLICT, "현재 다른 결제 요청을 처리 중입니다. 잠시 후 다시 시도해 주세요."),

    EMAIL_ALREADY_AUTHENTICATED(HttpStatus.BAD_REQUEST, "이미 이메일 인증이 완료된 상태입니다."),

    EXPIRED_AUTH_CODE(HttpStatus.GONE, "만료된 인증코드 입니다."),

    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "잘못된 인증코드 입니다."),

    INITIATE_EMAIL_REQUEST(HttpStatus.BAD_REQUEST, "먼저 이메일 인증 요청을 진행해주세요."),

    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),

    NO_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다."),

    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자 입니다."),

    AWS_SERVICE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "AWS 서비스 오류가 발생했습니다."),

    AWS_CLIENT_EXCEPTION(HttpStatus.BAD_REQUEST, "AWS 클라이언트 요청이 잘못되었습니다."),

    IO_EXCEPTION_ON_IMAGE_DELETE(HttpStatus.BAD_REQUEST, "이미지를 삭제하는 중 입출력 오류가 발생했습니다."),

    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 이미지가 존재하지 않습니다."),

    POST_IMAGES_NOT_FOUND(HttpStatus.NOT_FOUND, "공고에 등록된 이미지가 없습니다."),

    NOT_FOUND_IMAGE(HttpStatus.NOT_FOUND, "이미지가 존재하지 않습니다."),

    INVALID_ADDRESS_FORMAT(HttpStatus.NOT_FOUND, "올바른 주소 형식이 아닙니다."),

    INVALID_CATEGORY_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 카테고리 형식입니다."),

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다.");

    private final HttpStatus status;
    private final String message;
}
