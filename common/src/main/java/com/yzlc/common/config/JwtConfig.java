package com.yzlc.common.config;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Slf4j
@ToString
@Data
public class JwtConfig {
    private String ttlMinute;
    private String authorization = "Authorization";
    private String bearer = "Bearer ";
}
