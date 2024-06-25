package com.style.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author leon
 * @date 2024-06-13 14:27:19
 */
@MapperScan("com.style.persistence.mapper")
@ComponentScan(basePackages = {"com.style.auth","com.style.persistence"})
@SpringBootApplication
public class AuthServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
