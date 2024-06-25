package com.style.persistence.support.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.style.persistence.mapper.Oauth2RegisteredClientMapper;
import com.style.persistence.model.Oauth2RegisteredClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义mysql存储 注册client
 *
 * @author leon
 * @date 2024-06-21 16:28:37
 */
@Slf4j
@Component
public class CustomMysqlRegisteredClientRepository implements RegisteredClientRepository {

    private final Oauth2RegisteredClientMapper oauth2RegisteredClientMapper;

    public CustomMysqlRegisteredClientRepository(Oauth2RegisteredClientMapper oauth2RegisteredClientMapper) {
        this.oauth2RegisteredClientMapper = oauth2RegisteredClientMapper;
    }

    private static final String CLIENT_ID_NOT_EXIST_ERROR_CODE = "client not exist";

    private static final String ZONED_DATETIME_ZONE_ID = "Asia/Shanghai";


    @Override
    public void save(RegisteredClient registeredClient) {
        String clientId = registeredClient.getClientId();
        RegisteredClient client = this.findByClientId(clientId);
        if (null != client) {
            //更新
            String id = client.getId();

            Oauth2RegisteredClient Oauth2RegisteredClient = new Oauth2RegisteredClient();
            Oauth2RegisteredClient.setId(id);

            setProperties(registeredClient, Oauth2RegisteredClient);

            int update = oauth2RegisteredClientMapper.updateById(Oauth2RegisteredClient);
            log.info("update Oauth2RegisteredClient 更新结果:{}",update);
        } else {
            //插入
            Oauth2RegisteredClient Oauth2RegisteredClient = new Oauth2RegisteredClient();
            Oauth2RegisteredClient.setClientId(registeredClient.getClientId());
            Oauth2RegisteredClient.setClientName(registeredClient.getClientName());
            Oauth2RegisteredClient.setClientSecret(registeredClient.getClientSecret());

            setProperties(registeredClient, Oauth2RegisteredClient);

            int insert = oauth2RegisteredClientMapper.insert(Oauth2RegisteredClient);
            log.info("插入结果: {}", insert);
        }


    }

    private void setProperties(RegisteredClient registeredClient, Oauth2RegisteredClient oauth2RegisteredClient) {
        Instant clientIdIssuedAt = registeredClient.getClientIdIssuedAt();
        if (clientIdIssuedAt != null) {
            oauth2RegisteredClient.setClientIdIssuedAt(clientIdIssuedAt.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());
        }
        Instant clientSecretExpiresAt = registeredClient.getClientSecretExpiresAt();
        if (clientSecretExpiresAt != null) {
            oauth2RegisteredClient.setClientSecretExpiresAt(clientSecretExpiresAt.atZone(ZoneId.of("Asia/Shanghai")).toLocalDateTime());
        }

        Set<ClientAuthenticationMethod> clientAuthenticationMethods = registeredClient.getClientAuthenticationMethods();
        if (CollectionUtil.isNotEmpty(clientAuthenticationMethods)) {
            String str = clientAuthenticationMethods.stream().map(ClientAuthenticationMethod::getValue).collect(Collectors.joining(","));
            oauth2RegisteredClient.setClientAuthenticationMethods(str);
        }

        Set<AuthorizationGrantType> authorizationGrantTypes = registeredClient.getAuthorizationGrantTypes();
        if (CollectionUtil.isNotEmpty(authorizationGrantTypes)) {
            String str = authorizationGrantTypes.stream().map(AuthorizationGrantType::getValue).collect(Collectors.joining(","));
            oauth2RegisteredClient.setAuthorizationGrantTypes(str);
        }

        Set<String> redirectUris = registeredClient.getRedirectUris();
        if (CollectionUtil.isNotEmpty(redirectUris)) {
            String collect = String.join(",", redirectUris);
            oauth2RegisteredClient.setRedirectUris(collect);
        }

        Set<String> postLogoutRedirectUris = registeredClient.getPostLogoutRedirectUris();
        if (CollectionUtil.isNotEmpty(postLogoutRedirectUris)) {
            String collect = String.join(",", postLogoutRedirectUris);
            oauth2RegisteredClient.setPostLogoutRedirectUris(collect);
        }

        Set<String> scopes = registeredClient.getScopes();
        if (CollectionUtil.isNotEmpty(scopes)) {
            String collect = String.join(",", scopes);
            oauth2RegisteredClient.setScopes(collect);
        }

        TokenSettings tokenSettings = registeredClient.getTokenSettings();
        if (ObjectUtil.isNotNull(tokenSettings)) {
            oauth2RegisteredClient.setTokenSettings(JSON.toJSONString(tokenSettings.getSettings()));
        }
        ClientSettings clientSettings = registeredClient.getClientSettings();
        if (ObjectUtil.isNotNull(clientSettings)) {
            oauth2RegisteredClient.setClientSettings(JSON.toJSONString(clientSettings.getSettings()));
        }
    }

    @Override
    public RegisteredClient findById(String id) {

        Oauth2RegisteredClient oauth2RegisteredClient = oauth2RegisteredClientMapper.selectById(id);
        if (oauth2RegisteredClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(CLIENT_ID_NOT_EXIST_ERROR_CODE,
                    "Authorization client table data id not exist: " + id, null),
                    id);
        }
        return oauth2RegisteredClientConvert2RegisteredClient(oauth2RegisteredClient);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        LambdaQueryWrapper<Oauth2RegisteredClient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Oauth2RegisteredClient::getClientId, clientId);
        Oauth2RegisteredClient oauth2RegisteredClient = oauth2RegisteredClientMapper.selectOne(wrapper);
        if (oauth2RegisteredClient == null) {
            throw new ClientAuthorizationException(new OAuth2Error(CLIENT_ID_NOT_EXIST_ERROR_CODE,
                    "Authorization client id not exist: " + clientId, null),
                    clientId);
        }
        return oauth2RegisteredClientConvert2RegisteredClient(oauth2RegisteredClient);
    }

    /**
     * oauth2RegisteredClient 转换为 RegisteredClient
     *
     * @param oauth2RegisteredClient oauth2RegisteredClient
     * @return RegisteredClient
     */
    @SuppressWarnings("unchecked")
    private RegisteredClient oauth2RegisteredClientConvert2RegisteredClient(Oauth2RegisteredClient oauth2RegisteredClient) {
        String clientAuthenticationMethods = oauth2RegisteredClient.getClientAuthenticationMethods();
        Set<ClientAuthenticationMethod> clientAuthenticationMethodSet = new HashSet<>();
        if (StrUtil.isNotBlank(clientAuthenticationMethods)) {
            clientAuthenticationMethodSet = Arrays.stream(clientAuthenticationMethods.split(",")).map(ClientAuthenticationMethod::new).collect(Collectors.toSet());
        }

        String authorizationGrantTypes = oauth2RegisteredClient.getAuthorizationGrantTypes();
        Set<AuthorizationGrantType> authorizationGrantTypeSet = new HashSet<>();
        if (StrUtil.isNotBlank(authorizationGrantTypes)) {
            authorizationGrantTypeSet = Arrays.stream(authorizationGrantTypes.split(",")).map(AuthorizationGrantType::new).collect(Collectors.toSet());
        }

        String redirectUris = oauth2RegisteredClient.getRedirectUris();
        Set<String> redirectUriSet = new HashSet<>();
        if (StrUtil.isNotBlank(redirectUris)) {
            redirectUriSet = Arrays.stream(redirectUris.split(",")).collect(Collectors.toSet());
        }

        String postLogoutRedirectUris = oauth2RegisteredClient.getPostLogoutRedirectUris();
        Set<String> postLogoutRedirectUriSet = new HashSet<>();
        if (StrUtil.isNotBlank(postLogoutRedirectUris)) {
            postLogoutRedirectUriSet = Arrays.stream(postLogoutRedirectUris.split(",")).collect(Collectors.toSet());
        }

        String scopes = oauth2RegisteredClient.getScopes();
        Set<String> scopeSet = new HashSet<>();
        if (StrUtil.isNotBlank(scopes)) {
            scopeSet = Arrays.stream(scopes.split(",")).collect(Collectors.toSet());
        }

        String clientSettings = oauth2RegisteredClient.getClientSettings();
        Map<String, Object> clientSettingMap = JSON.parseObject(clientSettings, Map.class);

        String tokenSettings = oauth2RegisteredClient.getTokenSettings();
        Map<String, Object> tokenSettingsMap = JSON.parseObject(tokenSettings, Map.class);
        if (MapUtil.isNotEmpty(tokenSettingsMap)) {
            tokenSettingsMap.forEach((k, v) -> {
                //resolve java.lang.ClassCastException: class java.lang.String cannot be cast to class java.time.Duration (java.lang.String and java.time.Duration are in module java.base of loader 'bootstrap')
                if (k.contains("time-to-live")) {
                    Duration duration = Duration.parse(v.toString());
                    tokenSettingsMap.put(k, duration);
                }
                //java.lang.ClassCastException: class com.alibaba.fastjson2.JSONObject cannot be cast to class org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat (com.alibaba.fastjson2.JSONObject and org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat are in unnamed module of loader 'app')
                if("settings.token.access-token-format".equalsIgnoreCase(k)){
                    JSONObject jo = JSON.parseObject(v.toString(), JSONObject.class);
                    String tokenFormat = jo.getString("value");
                    if(StrUtil.isNotBlank(tokenFormat)){
                        tokenSettingsMap.put(k, new OAuth2TokenFormat(tokenFormat));
                    }
                }
                //java.lang.ClassCastException: class java.lang.String cannot be cast to class org.springframework.security.oauth2.jose.jws.SignatureAlgorithm (java.lang.String is in module java.base of loader 'bootstrap'; org.springframework.security.oauth2.jose.jws.SignatureAlgorithm is in unnamed module of loader 'app')
                //settings.token.id-token-signature-algorithm -> RS256
                if("settings.token.id-token-signature-algorithm".equalsIgnoreCase(k)){
                    tokenSettingsMap.put(k, SignatureAlgorithm.from(v.toString()));

                }
            });
        }


        Set<ClientAuthenticationMethod> finalClientAuthenticationMethodSet = clientAuthenticationMethodSet;
        Set<AuthorizationGrantType> finalAuthorizationGrantTypeSet = authorizationGrantTypeSet;
        Set<String> finalRedirectUriSet = redirectUriSet;
        Set<String> finalPostLogoutRedirectUriSet = postLogoutRedirectUriSet;
        Set<String> finalScopeSet = scopeSet;

        RegisteredClient.Builder builder = RegisteredClient
                .withId(oauth2RegisteredClient.getId())
                .clientId(oauth2RegisteredClient.getClientId())
                .clientSecret(oauth2RegisteredClient.getClientSecret())
                .clientIdIssuedAt(Optional.ofNullable(oauth2RegisteredClient.getClientIdIssuedAt())
                        .map(d -> d.atZone(ZoneId.of(ZONED_DATETIME_ZONE_ID)).toInstant())
                        .orElse(null))
                .clientSecretExpiresAt(Optional.ofNullable(oauth2RegisteredClient.getClientSecretExpiresAt())
                        .map(d -> d.atZone(ZoneId.of(ZONED_DATETIME_ZONE_ID)).toInstant())
                        .orElse(null))
                .clientName(oauth2RegisteredClient.getClientName())
                .clientAuthenticationMethods(c -> c.addAll(finalClientAuthenticationMethodSet))
                .authorizationGrantTypes(a -> a.addAll(finalAuthorizationGrantTypeSet))

                .redirectUris(r -> r.addAll(finalRedirectUriSet))

                .postLogoutRedirectUris(p -> p.addAll(finalPostLogoutRedirectUriSet))
                .scopes(s -> s.addAll(finalScopeSet))
                //// requireAuthorizationConsent(true) 不设置 授权页不会显示
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).settings(c -> c.putAll(clientSettingMap)).build())
                .tokenSettings(TokenSettings.builder().settings(c -> c.putAll(tokenSettingsMap)).build());
        return builder.build();

    }
}
