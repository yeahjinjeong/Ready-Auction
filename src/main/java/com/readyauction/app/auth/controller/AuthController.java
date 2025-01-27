package com.readyauction.app.auth.controller;

import com.readyauction.app.auth.dto.req.CreateAccessTokenRequest;
import com.readyauction.app.auth.dto.res.CreateAccessTokenResponse;
import com.readyauction.app.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
            @RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = authService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    @GetMapping("/login")
    public void login(@RequestParam(value = "success", required = false) Boolean success,
                        @RequestParam(value = "error", required = false) Boolean error,
                        Model model) {
        if (success != null && success) {
            model.addAttribute("loginSuccess", true);
        }

        if (error != null && error) {
            model.addAttribute("loginFailure", true);
        }
    }
}
