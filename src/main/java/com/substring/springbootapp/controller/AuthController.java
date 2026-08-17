package com.substring.springbootapp.controller;

import com.substring.springbootapp.dto.AuthResponse;
import com.substring.springbootapp.dto.LoginRequest;
import com.substring.springbootapp.dto.RefreshTokenRequest;
import com.substring.springbootapp.dto.RegisterRequest;
import com.substring.springbootapp.dto.TokenRefreshResponse;
import com.substring.springbootapp.entity.RefreshToken;
import com.substring.springbootapp.entity.Role;
import com.substring.springbootapp.entity.User;
import com.substring.springbootapp.security.JwtService;
import com.substring.springbootapp.service.RefreshTokenService;
import com.substring.springbootapp.service.UserService;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                           UserService userService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAuthResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userService.findByEmail(request.email());

        return ResponseEntity.ok(toAuthResponse(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken existing = refreshTokenService.findByToken(request.refreshToken());
        refreshTokenService.verifyExpiration(existing);
        User user = existing.getUser();

        // Rotate: invalidate the used refresh token and issue a new one.
        refreshTokenService.revoke(existing);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        String accessToken = jwtService.generateAccessToken(toUserDetails(user));
        return ResponseEntity.ok(new TokenRefreshResponse(accessToken, newRefreshToken.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.findByToken(request.refreshToken());
        refreshTokenService.revoke(token);
        return ResponseEntity.noContent().build();
    }

    private AuthResponse toAuthResponse(User user) {
        Set<String> roleNames = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(toUserDetails(user));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail(), user.getFullName(), roleNames);
    }

    private UserDetails toUserDetails(User user) {
        Set<String> roleNames = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(roleNames.stream().map(r -> "ROLE_" + r).toArray(String[]::new))
                .build();
    }
}
