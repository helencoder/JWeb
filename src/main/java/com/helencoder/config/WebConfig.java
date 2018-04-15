package com.helencoder.config;

import com.helencoder.domain.filter.RequestFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * Web应用基础配置
 *
 * Created by zhenghailun on 2018/3/26.
 */
@SpringBootApplication
public class WebConfig extends WebMvcConfigurerAdapter {

    @Resource
    private RequestFilter requestFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestFilter)
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**"); // 跨域过滤
    }
}
