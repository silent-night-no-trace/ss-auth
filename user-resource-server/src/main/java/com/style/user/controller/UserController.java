package com.style.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author leon
 * @date 2024-06-13 14:29:24
 */
@RestController
public class UserController {

    private final RestClient restClient;

    public UserController(@Value("${messages.base-uri}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @GetMapping(value = "/user/messages", params = "use_case=delegation")
    public List<String> getMessagesWithDelegation(
            @RegisteredOAuth2AuthorizedClient("messaging-client-token-exchange-with-delegation")
            OAuth2AuthorizedClient authorizedClient) {
        return getUserMessages(authorizedClient);
    }

    @GetMapping(value = "/user/messages", params = "use_case=impersonation")
    public List<String> getMessagesWithImpersonation(
            @RegisteredOAuth2AuthorizedClient("messaging-client-token-exchange-with-impersonation")
            OAuth2AuthorizedClient authorizedClient) {
        return getUserMessages(authorizedClient);
    }

    private List<String> getUserMessages(OAuth2AuthorizedClient authorizedClient) {
        // @formatter:off
        String[] messages = Objects.requireNonNull(
                this.restClient.get()
                        .uri("/messages")
                        .headers((headers) -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                        .retrieve()
                        .body(String[].class)
        );
        // @formatter:on

        List<String> userMessages = new ArrayList<>(Arrays.asList(messages));
        userMessages.add("%s has %d unread messages".formatted(authorizedClient.getPrincipalName(), messages.length));

        return userMessages;
    }
}
