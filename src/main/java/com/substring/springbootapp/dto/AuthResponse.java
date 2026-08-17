package com.substring.springbootapp.dto;

import java.util.Set;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String fullName,
        Set<String> roles
) {
}
