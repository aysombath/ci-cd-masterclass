package com.substring.springbootapp.service;

import com.substring.springbootapp.dto.MenuItemResponse;
import com.substring.springbootapp.entity.Role;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MenuService {

    private record MenuDefinition(String key, String label, String path, Role requiredRole) {
    }

    // requiredRole == null means visible to any authenticated user.
    private static final List<MenuDefinition> MENU = List.of(
            new MenuDefinition("dashboard", "Dashboard", "/dashboard", null),
            new MenuDefinition("profile", "My Profile", "/profile", null),
            new MenuDefinition("user-management", "User Management", "/admin/users", Role.SYSADMIN),
            new MenuDefinition("role-permissions", "Roles & Permissions", "/admin/roles", Role.SYSADMIN),
            new MenuDefinition("reports", "Reports", "/admin/reports", Role.SYSADMIN),
            new MenuDefinition("settings", "System Settings", "/admin/settings", Role.SYSADMIN)
    );

    public List<MenuItemResponse> menuForRoles(Set<Role> roles) {
        boolean isSysAdmin = roles.contains(Role.SYSADMIN);

        return MENU.stream()
                .filter(item -> item.requiredRole() == null || isSysAdmin)
                .map(item -> new MenuItemResponse(item.key(), item.label(), item.path()))
                .toList();
    }
}
