package com.swarnlink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class JwtUserPrincipal implements UserDetails {
    private final Long userId;
    private final String shopName;
    private final String city;
    private final String phoneNumber;
    private final String fullName;
    private final List<String> roles;

    public String getShopName() {
        return shopName;
    }

    public String getCity() {
        return city;
    }

    public String getFullName() {
        return fullName;
    }

    public JwtUserPrincipal(Long userId, String shopName, String city, String phoneNumber, String fullName, List<String> roles) {
        this.userId = userId;
        this.shopName = shopName;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
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