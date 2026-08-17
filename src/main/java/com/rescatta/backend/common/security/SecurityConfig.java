package com.rescatta.backend.common.security;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<Filter> firebaseAuthenticationFilter() {

        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new FirebaseAuthenticationFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);

        return registration;
    }
}