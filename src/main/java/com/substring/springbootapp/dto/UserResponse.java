package com.substring.springbootapp.dto;

import com.substring.springbootapp.entity.Role;
import com.substring.springbootapp.entity.User;
import java.time.Instant;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        boolean enabled,
        Set<Role> roles,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(),
                user.isEnabled(), user.getRoles(), user.getCreatedAt());
    }
}
