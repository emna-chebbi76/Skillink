package com.skillink.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank private String nom;
        @NotBlank private String prenom;
        @Email @NotBlank private String email;
        @NotBlank @Size(min = 6) private String password;
        private String telephone;
        private String role = "client"; // client | prestataire
    }

    @Data
    public static class AuthResponse {
        private String token;
        private UserDto user;

        public AuthResponse(String token, UserDto user) {
            this.token = token;
            this.user = user;
        }
    }
}
