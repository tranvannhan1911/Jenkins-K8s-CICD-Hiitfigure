package com.nico.store.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author Duy Trần Thế
 * @version 1.0
 * @datetime 12/13/2021 8:57 PM
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver() {
            @Override
            public boolean isMultipart(HttpServletRequest request) {
                List<String> listMethod = Arrays.asList("PUT", "POST");
                String method = request.getMethod();
                if (!listMethod.contains(method)) {
                    return false;
                }
                String contentType = request.getContentType();
                return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
            }
        };
        multipartResolver.setDefaultEncoding("utf-8");
        return multipartResolver;
    }
}
