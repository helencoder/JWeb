package com.helencoder.domain.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求过滤
 *
 * Created by zhenghailun on 2018/3/26.
 */
@Component
public class RequestFilter extends HandlerInterceptorAdapter{

    private static final String REQUEST_START_KEY = "start_time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 记录请求信息
        String requestStr = String.format("request uri = %s, method = %s, query string = %s",
                request.getRequestURI(), request.getMethod(), request.getQueryString());
        System.out.println(requestStr);
        // 记录当前请求开始时间
        request.setAttribute(REQUEST_START_KEY, System.currentTimeMillis());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute(REQUEST_START_KEY);
        long duration = System.currentTimeMillis() - startTime;
        //String responseException = (String) request.getAttribute(WebConstants.RESPONSE_EXCEPTION);
        boolean isSuccess = true;
        if (ex != null) {
            isSuccess = false;
        }
        String responseStr = String.format("request uri = %s, time(ms) = %s, success = %s",
                request.getRequestURI(), duration, isSuccess);
        System.out.println(responseStr);
    }
}
