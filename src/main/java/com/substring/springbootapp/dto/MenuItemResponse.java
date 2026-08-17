package com.substring.springbootapp.dto;

public record MenuItemResponse(
        String key,
        String label,
        String path
) {
}
