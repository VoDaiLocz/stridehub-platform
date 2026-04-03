package com.stridehub.identity.web;

import com.stridehub.identity.domain.User;
import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String name,
        String firstName,
        String lastName,
        boolean emailVerified,
        String status,
        List<String> roles
) {

    public static UserProfileResponse from(User user) {
        List<String> roles = user.getRoles().stream().map(role -> role.getCode()).sorted().toList();
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getFirstName(),
                user.getLastName(),
                user.isEmailVerified(),
                user.getStatus().name(),
                roles
        );
    }
}
