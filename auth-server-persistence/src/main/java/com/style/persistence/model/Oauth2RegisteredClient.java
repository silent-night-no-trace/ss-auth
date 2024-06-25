package com.style.persistence.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 注册client db 对象
 *
 * @author leon
 * @date 2024-06-21 16:28:37
 */
@Data
@TableName("oauth2_registered_client")
public class Oauth2RegisteredClient implements Serializable {

    private static final long serialVersionUID = 2525406353184623019L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 客户端密钥
     */
    private String clientSecret;

    private LocalDateTime clientIdIssuedAt;

    private LocalDateTime clientSecretExpiresAt;

    private String clientAuthenticationMethods;

    private String authorizationGrantTypes;

    private String redirectUris;

    private String postLogoutRedirectUris;

    private String scopes;

    private String clientSettings;

    private String tokenSettings;
}
