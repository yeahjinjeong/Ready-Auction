package com.readyauction.app.auth.dto.req;

import lombok.Data;

@Data
public class CreateAccessTokenRequest {
    private String refreshToken;
}
