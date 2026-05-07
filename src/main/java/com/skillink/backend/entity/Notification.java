package com.skillink.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Builder.Default
    private String time = "À l'instant";

    @Builder.Default
    private String type = "info"; // info | success | warning | error

    @Builder.Default
    private Boolean read = false;
}
