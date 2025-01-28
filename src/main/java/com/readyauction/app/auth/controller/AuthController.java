package com.readyauction.app.auth.controller;

import com.readyauction.app.auth.config.jwt.JwtProvider;
import com.readyauction.app.auth.dto.request.CreateAccessTokenRequest;
import com.readyauction.app.auth.dto.response.CreateAccessTokenResponse;
import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
            @RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = authService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    @GetMapping("/login")
    public void login(
//            @RequestParam(value = "success", required = false) Boolean success,
//            @RequestParam(value = "error", required = false) Boolean error,
//            Model model
    ) {
//        if (success != null && success) {
//            model.addAttribute("loginSuccess", true);
//        }
//
//        if (error != null && error) {
//            model.addAttribute("loginFailure", true);
//        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email,
                                   @RequestParam("password") String password,
                                   HttpSession session
    ) {
        Authentication authentication = authenticateUser(email, password);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }

        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        String accessToken = jwtProvider.createAccessToken(principal.getUser());
        ResponseCookie responseCookie = createResponseCookie(principal);

        HttpHeaders headers = setHeaders(accessToken, responseCookie);

        return ResponseEntity.ok()
                .headers(headers)
                .body("로그인 성공");
    }

    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authenticationToken);  // authenticationManager를 통해 인증 처리
    }

    private ResponseCookie createResponseCookie(AuthPrincipal principal) {
        return ResponseCookie.from(
                        "refreshToken",
                        authService.findRefreshTokenByUserId(principal.getUser().getId())
                )
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 7)  // 7일간 유효
                .build();
    }

    private HttpHeaders setHeaders(String accessToken, ResponseCookie responseCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.set("Set-Cookie", responseCookie.toString());
        return headers;
    }
}
