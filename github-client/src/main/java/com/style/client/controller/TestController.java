package com.style.client.controller;

import com.style.client.model.GithubOAuth2User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leon
 * @date 2024-06-13 14:29:24
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public GithubOAuth2User hello() {
        return (GithubOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
