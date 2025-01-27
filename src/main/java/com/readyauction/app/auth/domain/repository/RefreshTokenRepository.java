package com.readyauction.app.auth.domain.repository;

import com.readyauction.app.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    default RefreshToken findUserByRefreshToken(String refreshToken) {
        return findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    default RefreshToken findRefreshTokenByUserId_(Long userId) {
        return findRefreshTokenByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    Optional<RefreshToken> findRefreshTokenByUserId(Long userId);
}
