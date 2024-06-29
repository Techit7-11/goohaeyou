package com.ll.goohaeyou.global.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomHttpStatus{
    private int statusCode;
    private String statusMessage;
}
