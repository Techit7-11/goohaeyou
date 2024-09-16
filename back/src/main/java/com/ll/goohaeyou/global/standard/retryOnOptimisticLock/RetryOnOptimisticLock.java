package com.ll.goohaeyou.global.standard.retryOnOptimisticLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnOptimisticLock {
    int attempts() default 3;   // 재시도할 최대 횟수
    long backoff() default 1000L;    // 재시도 사이의 대기 시간 (1초)
}