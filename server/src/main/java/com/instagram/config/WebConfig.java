package com.instagram.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Autowired
    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry
            .addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/user/search", "/api/user/profile", "/api/post", "/api/user/post", "/api/follow", "/api/like", "/api/post/like", "/api/comment/**");
    }
}
