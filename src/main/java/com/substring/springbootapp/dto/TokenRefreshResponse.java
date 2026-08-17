package com.substring.springbootapp.dto;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}
