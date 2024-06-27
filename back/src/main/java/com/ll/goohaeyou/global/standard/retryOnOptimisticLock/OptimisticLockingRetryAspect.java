package com.ll.goohaeyou.global.standard.retryOnOptimisticLock;

import com.ll.goohaeyou.global.exception.payment.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Aspect   // AOP를 통해 추가적인 행동 정의
@Order(Ordered.LOWEST_PRECEDENCE - 1) // @Transactional보다 더 먼저 메서드에 적용되어야 해서, 우선순위를 높게 설정
@Component
@Slf4j
public class OptimisticLockingRetryAspect {

    // @Around를 통해 @RetryOnOptimisticLock 이 적용된 모든 메서드의 실행 전후, 예외 발생 시에 추가적인 로직을 수행
    @Around("@annotation(retryOnOptimisticLock)")
    public Object around(ProceedingJoinPoint joinPoint, RetryOnOptimisticLock retryOnOptimisticLock) throws Throwable {
        // 최대 시도 횟수와 대기 시간을 @RetryOnOptimisticLock 어노테이션에서 가져온다.
        int maxAttempts = retryOnOptimisticLock.attempts();
        long backoff = retryOnOptimisticLock.backoff();
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {    // 설정된 최대 시도 횟수까지 반복
            try {
                return joinPoint.proceed();  // 낙관적 락 재시도를 적용할 메서드 실행
            } catch (OptimisticLockingFailureException e) {
                lastException = e;
                log.info("Optimistic locking failure in attempt {} of {}", attempt, maxAttempts, e);
                // 설정된 최대 시도 횟수에 도달하지 않았다면, 지정된 대기 시간만큼 대기 후 재시도
                if (attempt < maxAttempts) {
                    Thread.sleep(backoff);
                }
            }
        }
        // 모든 시도가 실패했을 때의 예외 처리
        log.error("Optimistic locking failure: Max attempts exceeded.");
        throw new PaymentException.PaymentRequestConflictException();
    }
}
