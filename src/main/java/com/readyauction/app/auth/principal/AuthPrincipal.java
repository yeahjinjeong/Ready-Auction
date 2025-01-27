package com.readyauction.app.auth.principal;

import com.readyauction.app.user.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * UserDetailsService 구현체 (AuthService)가 조회한 결과를 UserDetails타입으로 제공한다.
 * 다음 3가지 정보를 가지고 있어야 한다.
 * - 아이디(username) String
 * - 비밀번호(password) String
 * - 권한(authorities) List<GrantedAuthority>
 *
 * </pre>
 */
@Data
public class AuthPrincipal implements UserDetails, Serializable {
    private final User user;

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                .map((authority) -> new SimpleGrantedAuthority(authority.name()))
                .toList();
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
