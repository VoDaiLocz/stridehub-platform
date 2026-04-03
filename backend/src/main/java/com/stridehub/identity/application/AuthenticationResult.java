package com.stridehub.identity.application;

import com.stridehub.identity.domain.User;

public record AuthenticationResult(
        String accessToken,
        String refreshToken,
        User user
) {
}
