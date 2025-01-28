package com.readyauction.app.auth.config.jwt;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.auth.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

import static com.readyauction.app.auth.utils.CookieUtils.getCookie;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();
        String accessToken = jwtProvider.createAccessToken(principal.getUser());

        // 액세스 토큰을 Authorization 헤더에 추가
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // 리프레시 토큰을 쿠키에 추가
        ResponseCookie responseCookie = ResponseCookie.from(
                "refreshToken",
                        authService.findRefreshTokenByUserId(principal.getUser().getId())
                )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)  // 7일간 유효
                .build();

        response.addHeader("Set-Cookie", responseCookie.toString());

        // 성공적인 로그인 응답
        response.sendRedirect("/auction");
        super.onAuthenticationSuccess(request, response, authentication);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
