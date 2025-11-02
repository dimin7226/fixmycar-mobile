package com.fixmycar.service;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VisitInterceptor implements HandlerInterceptor {

    private final VisitService visitService;

    public VisitInterceptor(VisitService visitService) {
        this.visitService = visitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) {
        String url = request.getRequestURI();
        visitService.incrementVisitCount(url);
        return true;
    }
}