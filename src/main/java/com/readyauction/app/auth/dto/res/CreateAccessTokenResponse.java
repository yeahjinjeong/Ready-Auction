package com.readyauction.app.auth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccessTokenResponse {
    private String accessToken;
}
