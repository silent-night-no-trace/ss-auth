package com.style.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author leon
 * {@code @date} 2024-06-13 14:47:56
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class ResourceServerConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .securityMatcher("/user/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/user/**").hasAuthority("SCOPE_user.read")
                )
                .oauth2ResourceServer((oauth2ResourceServer) -> oauth2ResourceServer
                        .jwt(Customizer.withDefaults())
                )
                .oauth2Client(Customizer.withDefaults());
        // @formatter:on

        return http.build();
    }

}