package com.substring.springbootapp.service;

import com.substring.springbootapp.dto.RegisterRequest;
import com.substring.springbootapp.entity.Role;
import com.substring.springbootapp.entity.User;
import com.substring.springbootapp.exception.EmailAlreadyExistsException;
import com.substring.springbootapp.repository.RefreshTokenRepository;
import com.substring.springbootapp.repository.UserRepository;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setFullName(request.fullName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        user.setRoles(Set.of(Role.USER));

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with id " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email " + email));
    }

    @Transactional
    public User updateRoles(Long id, Set<Role> roles) {
        User user = findById(id);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public User setEnabled(Long id, boolean enabled) {
        User user = findById(id);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        refreshTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }
}
