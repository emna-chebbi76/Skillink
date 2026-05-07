package com.skillink.backend.service;

import com.skillink.backend.dto.AuthDto;
import com.skillink.backend.dto.UserDto;
import com.skillink.backend.entity.Role;
import com.skillink.backend.entity.User;
import com.skillink.backend.repository.UserRepository;
import com.skillink.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authManager;

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Utilisateur introuvable"));

        String token = tokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        return new AuthDto.AuthResponse(token, UserDto.from(user));
    }

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");

        Role role = switch (req.getRole().toLowerCase()) {
            case "prestataire" -> Role.PRESTATAIRE;
            case "admin"       -> Role.ADMIN;
            default            -> Role.CLIENT;
        };

        User user = User.builder()
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .telephone(req.getTelephone())
                .role(role)
                .build();

        user = userRepository.save(user);
        String token = tokenProvider.generateToken(user.getEmail(), user.getId(), user.getRole().name());
        return new AuthDto.AuthResponse(token, UserDto.from(user));
    }
}
