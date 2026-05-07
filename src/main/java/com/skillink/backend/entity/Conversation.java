package com.skillink.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // IDs des deux participants (JSON: [1, 2])
    @ElementCollection
    @CollectionTable(name = "conversation_participants", joinColumns = @JoinColumn(name = "conversation_id"))
    @Column(name = "user_id")
    @Builder.Default
    private java.util.List<Long> participantIds = new java.util.ArrayList<>();

    // Noms sérialisés en JSON string: {"1":"Alice","2":"Bob"}
    @Column(columnDefinition = "TEXT")
    private String participantNames;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    private LocalDateTime lastMessageTime;

    // Compteurs non-lus sérialisés en JSON string: {"1":0,"2":3}
    @Column(columnDefinition = "TEXT")
    private String unreadCount;
}
