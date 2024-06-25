package com.style.persistence.support.user;

import com.style.persistence.mapper.SysUserMapper;
import com.style.persistence.model.SysUser;
import com.style.persistence.userdetails.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * @author leon
 * @date 2024-06-22 14:26:07
 */
@Component
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    public CustomUserDetailsServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.getUserByUsername(username);
        if (null == sysUser) {
            throw new UsernameNotFoundException("未知的用户: " + username);
        }
        String password = sysUser.getPassword();
        String phone = sysUser.getPhone();
        String phoneEncrypted = sysUser.getPhoneEncrypted();
        String avatar = sysUser.getAvatar();
        Integer status = sysUser.getStatus();

        // 后续可自行扩展 查询用户的权限
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("/oauth2/token", "/oauth2/authorize", "/authorized");

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUsername(username);
        customUserDetails.setPassword(password);
        customUserDetails.setPhone(phone);
        customUserDetails.setPhoneEncrypted(phoneEncrypted);
        customUserDetails.setAvatar(avatar);
        customUserDetails.setStatus(status);
        customUserDetails.setAuthorities(new HashSet<>(authorityList));
        return customUserDetails;
    }
}
