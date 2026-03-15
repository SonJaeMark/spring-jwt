package com.github.sonjaemark.spring_jwt.auth;

import com.github.sonjaemark.spring_jwt.dto.AuthResponse;
import com.github.sonjaemark.spring_jwt.dto.LoginRequest;
import com.github.sonjaemark.spring_jwt.dto.RegisterRequest;
import com.github.sonjaemark.spring_jwt.user.Role;
import com.github.sonjaemark.spring_jwt.user.User;
import com.github.sonjaemark.spring_jwt.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow();

        String token =
            jwtService.generateToken(user.getId(), user.getUsername());

        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getRole().name()
        );
    }

    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole() != null ? request.getRole() : Role.USER)
            .build();

        userRepository.save(user);

        String token =
            jwtService.generateToken(user.getId(), user.getUsername());

        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getRole().name()
        );
    }
}