package com.substring.springbootapp.dto;

import com.substring.springbootapp.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UpdateRolesRequest(
        @NotEmpty Set<Role> roles
) {
}
