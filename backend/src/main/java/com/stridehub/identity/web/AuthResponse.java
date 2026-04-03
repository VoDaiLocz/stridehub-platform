package com.stridehub.identity.web;

import com.stridehub.identity.application.AuthenticationResult;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfileResponse user
) {

    public static AuthResponse from(AuthenticationResult result) {
        return new AuthResponse(
                result.accessToken(),
                result.refreshToken(),
                UserProfileResponse.from(result.user())
        );
    }
}
