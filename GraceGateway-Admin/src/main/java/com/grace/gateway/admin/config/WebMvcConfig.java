package com.grace.gateway.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm:ss"));
        registrar.registerFormatters(registry);
    }

    @Bean
    public org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.Format getFormat() {
        org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.Format format = new org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties.Format();
        format.setDate("yyyy-MM-dd");
        format.setDateTime("yyyy-MM-dd HH:mm:ss");
        format.setTime("HH:mm:ss");
        return format;
    }
}
