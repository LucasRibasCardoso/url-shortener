package com.app.url_shortener.shared.config;


import com.app.url_shortener.security.config.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class, IdempotencyProperties.class})
@Configuration
public class PropertiesConfig {
}
