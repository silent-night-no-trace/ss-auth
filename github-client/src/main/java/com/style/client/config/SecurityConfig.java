package com.style.client.config;

import com.style.client.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author leon
 * @date 2024-06-13 14:47:56
 */
@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeHttpRequests(registry -> {
            registry.anyRequest().authenticated();
        })
                .oauth2Login(oauth2Login -> {
                    oauth2Login.userInfoEndpoint(config->{
                        // 自定义userService
                        config.userService(new CustomOAuth2UserService());
                    });
        });

        return http.build();
        // @formatter:on
    }


}