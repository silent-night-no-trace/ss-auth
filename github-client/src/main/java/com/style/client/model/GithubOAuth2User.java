package com.style.client.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 * @date 2024-06-13 18:04:52
 */
@Data
public class GithubOAuth2User implements OAuth2User {
    private List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes;
    private String id;
    private String name;
    private String login;
    private String email;


    public GithubOAuth2User(Map<String, Object> attributes, List<GrantedAuthority> authorities,String userNameAttributeName) {
        this.attributes = attributes;
        this.authorities = authorities;
        if(null != attributes){
            this.id = String.valueOf(attributes.get("id"));
            this.name = String.valueOf(attributes.get(userNameAttributeName));
            this.login = String.valueOf(attributes.get("login"));
            this.email = String.valueOf(attributes.get("email"));
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (null == attributes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("name", name);
            map.put("login", login);
            map.put("email", email);
            return map;
        }
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }
}
