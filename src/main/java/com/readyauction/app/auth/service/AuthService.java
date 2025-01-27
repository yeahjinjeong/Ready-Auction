package com.readyauction.app.auth.service;

import com.readyauction.app.auth.config.jwt.JwtProvider;
import com.readyauction.app.auth.domain.RefreshToken;
import com.readyauction.app.auth.domain.repository.RefreshTokenRepository;
import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.entity.User;
import com.readyauction.app.user.repository.MemberRepository;
import com.readyauction.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new AuthPrincipal(user);
    }

    public String createNewAccessToken(String refreshToken) {
        if (!jwtProvider.validateAccessToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected refreshToken");
        }

        RefreshToken rt = findByRefreshToken(refreshToken);
        User user = findById(rt.getUserId());

        return jwtProvider.createAccessToken(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public String findRefreshTokenByUserId(Long userId) {
        return refreshTokenRepository.findRefreshTokenByUserId_(userId).getRefreshToken();
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findUserByRefreshToken(refreshToken);
    }

    /**
     * db의 변경된 회원정보를 SecurityContext의 Authentication에 반영하기
     * @param email
     */
    public void updateAuthentication(String email) {
        User newUser = userRepository.findMemberByEmail(email)
                                .orElseThrow();
        AuthPrincipal authPrincipal = new AuthPrincipal(newUser);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
            authPrincipal,
            authPrincipal.getPassword(),
            authPrincipal.getAuthorities()
        );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(newAuthentication);
    }
}
