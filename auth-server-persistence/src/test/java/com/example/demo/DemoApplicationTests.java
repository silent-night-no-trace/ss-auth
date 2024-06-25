package com.example.demo;

import com.style.persistence.PersistenceApplication;
import com.style.persistence.support.client.CustomMysqlRegisteredClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;


@SpringBootTest(classes = PersistenceApplication.class)
class DemoApplicationTests {

    @Autowired
    private CustomMysqlRegisteredClientRepository registeredClientRepository;

    @Test
    void contextLoads() {
        RegisteredClient messagingClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8888/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://127.0.0.1:8888/authorized")
                .postLogoutRedirectUri("http://127.0.0.1:8888/logged-out")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .scope("user.read")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        RegisteredClient deviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("device-messaging-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope("message.read")
                .scope("message.write")
                .build();

        RegisteredClient tokenExchangeClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("token-client")
                .clientSecret("{noop}token")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(new AuthorizationGrantType("urn:ietf:params:oauth:grant-type:token-exchange"))
                .scope("message.read")
                .scope("message.write")
                .build();

        // Save registered client's in db
        registeredClientRepository.save(messagingClient);
        registeredClientRepository.save(deviceClient);
        registeredClientRepository.save(tokenExchangeClient);
        System.out.println(registeredClientRepository.findById("d153f77279afd02a61c4f2a2797426d7"));
        System.out.println(registeredClientRepository.findById("cc7c958a01dd02be7a39d88653d1f856"));
    }


}
