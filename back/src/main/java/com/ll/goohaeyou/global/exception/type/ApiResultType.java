package com.ll.goohaeyou.global.exception.type;

import lombok.Getter;

@Getter
public enum ApiResultType {
    SUCCESS,
    VALIDATION_EXCEPTION,
    CUSTOM_EXCEPTION
}
