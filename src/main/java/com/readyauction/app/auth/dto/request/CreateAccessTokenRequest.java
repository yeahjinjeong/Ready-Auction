package com.readyauction.app.auth.dto.request;

import lombok.Data;

@Data
public class CreateAccessTokenRequest {
    private String refreshToken;
}
