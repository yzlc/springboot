package com.yzlc.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowCredentials(true)
				.allowedMethods("GET", "POST", "DELETE", "PUT").maxAge(3600 * 24);
	}

	@Bean
	public TokenInterceptor tokenInterceptor() {
		return new TokenInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration addInterceptor = registry.addInterceptor(tokenInterceptor());
		addInterceptor.excludePathPatterns("/login");
		addInterceptor.addPathPatterns("/**");
	}
}