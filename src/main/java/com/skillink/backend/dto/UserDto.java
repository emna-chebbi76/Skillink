package com.skillink.backend.dto;

import com.skillink.backend.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String quartier;
    private String bio;
    private String role;
    private String avatar;
    private LocalDateTime createdAt;

    public static UserDto from(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setEmail(u.getEmail());
        dto.setTelephone(u.getTelephone());
        dto.setQuartier(u.getQuartier());
        dto.setBio(u.getBio());
        dto.setRole(u.getRole().name().toLowerCase());
        dto.setAvatar(u.getAvatar());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }
}
