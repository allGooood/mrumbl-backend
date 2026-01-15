package com.mrumbl.backend.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String controller = signature.getDeclaringType().getSimpleName();
        
        // 서블릿 관련 파라미터 제외하고 필터링
        Object[] filteredArgs = filterServletParameters(joinPoint.getArgs(), signature.getParameterTypes());
        Object maskedArgs = filteredArgs.length > 0 ? MaskingUtil.mask(filteredArgs) : filteredArgs;

        // 1. Business 로직 수행 전
        log.info("[{} provoked.] {} {} args={}", controller, method, uri, maskedArgs);

        try {
            Object result = joinPoint.proceed();
            long time = System.currentTimeMillis() - start;

            // 2. Business 로직 수행 후
            // 느린 요청만 완료 로그 출력 (예: 1초 이상)
            if (time > 1000) {
                log.warn("[{} completed.] {} {} elapsed={}ms (SLOW)", controller, method, uri, time);
            }
            return result;

        } catch (Exception e) {
            // 3. Error 발생
            long time = System.currentTimeMillis() - start;
            log.error("[{} failed.] {} {} elapsed={}ms error={}", controller, method, uri, time, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 서블릿 관련 파라미터(HttpServletRequest, HttpServletResponse 등)를 필터링
     */
    private Object[] filterServletParameters(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }

        List<Object> filtered = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            Class<?> paramType = parameterTypes[i];
            
            // 서블릿 관련 타입 제외
            if (shouldExcludeFromLogging(paramType)) {
                continue;
            }
            
            filtered.add(args[i]);
        }
        
        return filtered.toArray();
    }

    /**
     * 로깅에서 제외할 파라미터 타입인지 확인
     */
    private boolean shouldExcludeFromLogging(Class<?> paramType) {
        if (paramType == null) {
            return false;
        }
        
        String className = paramType.getName();
        
        // 서블릿 관련 클래스 제외
        return className.startsWith("jakarta.servlet.") ||
               className.startsWith("javax.servlet.") ||
               paramType == HttpServletRequest.class ||
               paramType == HttpServletResponse.class ||
               // Spring Web 관련 일부 클래스 제외 (필요시 추가)
               className.startsWith("org.springframework.web.servlet.");
    }

}
