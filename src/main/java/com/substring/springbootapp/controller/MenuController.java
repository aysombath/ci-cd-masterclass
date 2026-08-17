package com.substring.springbootapp.controller;

import com.substring.springbootapp.dto.MenuItemResponse;
import com.substring.springbootapp.entity.Role;
import com.substring.springbootapp.service.MenuService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<MenuItemResponse> getMenu(Authentication authentication) {
        Set<Role> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        return menuService.menuForRoles(roles);
    }
}
