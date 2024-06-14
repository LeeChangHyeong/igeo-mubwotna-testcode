package com.sparta.igeomubwotna.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "RequestInformationAop")
@Aspect
@Component
@RequiredArgsConstructor
public class RequestInformationAop {
    @Pointcut("execution(* com.sparta.igeomubwotna.controller.CommentController.*(..))")
    private void comment() {}

    @Pointcut("execution(* com.sparta.igeomubwotna.controller.LikeController.*(..))")
    private void like() {}

    @Pointcut("execution(* com.sparta.igeomubwotna.controller.RecipeController.*(..))")
    private void recipe() {}

    @Pointcut("execution(* com.sparta.igeomubwotna.controller.UserController.*(..))")
    private void user() {}

    @Before("comment() || like() || recipe() || user()")
    public void logRequestInfo() {
        // 현재 요청의 속성들을 가져옴
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            // HttpServletRequest 객체를 통해 요청 URL과 HTTP 메서드를 가져옴
            HttpServletRequest request = attributes.getRequest();
            // 로그 출력
            log.info("Request URL: {}, HTTP Method: {}", request.getRequestURL(), request.getMethod());
        }
    }
}
