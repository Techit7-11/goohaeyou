package com.ll.goohaeyou.global.exception;

import lombok.Getter;

@Getter
public class GoohaeyouException extends RuntimeException {
    private final ErrorCode errorCode;

    public GoohaeyouException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
