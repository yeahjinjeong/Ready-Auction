package com.readyauction.app.auth.principal;

import com.readyauction.app.user.entity.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

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
    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Member#authorities:Set<Authority> -> List<SimpleGrantedAuthority>
        // SimpleGrantedAuthority는 String을 인자로 하는 생성자 제공.
        return member.getAuthorities().stream()
                .map((authority) -> new SimpleGrantedAuthority(authority.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    /**
     * 계정이 만료되지 않았는가?
     * - true(만료되지 않았음)
     * - false(만료되었음)
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨있지 않은가?
     * - true(잠겨있지 않음)
     * - false(잠겨있음)
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호가 만료되지 않았는가?
     * - true(만료되지 않았음)
     * - false(만료되었음)
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 활성화 되었는가?
     * - true(활성화 되었음)
     * - false(비활성화 되었음)
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
