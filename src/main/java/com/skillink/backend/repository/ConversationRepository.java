package com.skillink.backend.repository;

import com.skillink.backend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participantIds p WHERE p = :userId")
    List<Conversation> findByParticipant(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE :u1 MEMBER OF c.participantIds AND :u2 MEMBER OF c.participantIds")
    Optional<Conversation> findByTwoParticipants(@Param("u1") Long u1, @Param("u2") Long u2);
}
