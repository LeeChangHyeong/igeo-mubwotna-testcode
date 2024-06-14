package com.sparta.igeomubwotna.security;

import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.entity.UserStatusEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Override 메서드라 메서드명이 고정입니다.
    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 상태를 가져옴
        UserStatusEnum status = user.getStatus();
        // 상태를 권한 문자열로 변환
        String authority = status.getStatus();

        // 권한 문자열을 SimpleGrantedAuthority 객체로 만듦
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        // 권한을 저장할 컬렉션을 생성하고 추가
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}