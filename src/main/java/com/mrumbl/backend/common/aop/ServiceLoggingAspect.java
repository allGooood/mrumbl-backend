package com.mrumbl.backend.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.toShortString();
        Object[] args = joinPoint.getArgs();
        String service = signature.getDeclaringType().getSimpleName();

        // Service는 debug 레벨로 진입 로그만 출력 (args는 마스킹 처리)
        Object maskedArgs = args.length > 0 ? MaskingUtil.mask(args) : args;
        log.debug("[{} provoked.] {} args={}", service, method, maskedArgs);

        try{
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;
            
            // 느린 메서드만 완료 로그 출력 (예: 500ms 이상)
            if (time > 500) {
                log.warn("[{} completed.] {} elapsed={}ms (SLOW)", service, method, time);
            }
            return result;
        } catch (Exception e){
            long time = System.currentTimeMillis() - start;
            log.error("[{} failed.] {} elapsed={}ms message={}", service, method, time, e.getMessage(), e);
            throw e;
        }
    }
}
