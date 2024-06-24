package com.ll.goohaeyou.standard.retryOnOptimisticLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드에 낙관적 락 재시도 로직을 적용하기 위한 사용자 정의 어노테이션.
 * 이 어노테이션이 적용된 메서드는 낙관적 락 실패(OptimisticLockingFailureException) 시
 * 지정된 시도 횟수와 대기 시간을 갖고 자동으로 재시도를 수행한다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnOptimisticLock {
    int attempts() default 3;   // 재시도할 최대 횟수

    long backoff() default 1000L;    // 재시도 사이의 대기 시간 (1초)
}
