package com.skillink.backend.controller;

import com.skillink.backend.entity.*;
import com.skillink.backend.exception.ResourceNotFoundException;
import com.skillink.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessagingController {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    // ── MESSAGES ────────────────────────────────────────────────────────────

    @GetMapping("/messages")
    public ResponseEntity<List<Map<String, Object>>> getMessages(
            @RequestParam(required = false) Long conversationId) {
        List<Message> msgs;
        if (conversationId != null) {
            Conversation conv = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation non trouvée"));
            msgs = messageRepository.findByConversationOrderByTimestampAsc(conv);
        } else {
            msgs = messageRepository.findAll();
        }
        return ResponseEntity.ok(msgs.stream().map(this::toMessageMap).toList());
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> body) throws Exception {
        Long convId = toLong(body.get("conversationId"));
        Conversation conv = conversationRepository.findById(convId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation non trouvée"));

        Message msg = Message.builder()
                .conversation(conv)
                .senderId(toLong(body.get("senderId")))
                .receiverId(toLong(body.get("receiverId")))
                .senderName((String) body.get("senderName"))
                .receiverName((String) body.get("receiverName"))
                .content((String) body.get("content"))
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .fileUrl((String) body.get("fileUrl"))
                .fileName((String) body.get("fileName"))
                .fileType((String) body.get("fileType"))
                .build();

        Message saved = messageRepository.save(msg);

        // Mettre à jour lastMessage de la conversation
        conv.setLastMessage(msg.getContent());
        conv.setLastMessageTime(LocalDateTime.now());
        // Incrémenter unreadCount pour le destinataire
        Map<String, Integer> unread = parseUnread(conv.getUnreadCount());
        String receiverKey = String.valueOf(msg.getReceiverId());
        unread.put(receiverKey, unread.getOrDefault(receiverKey, 0) + 1);
        conv.setUnreadCount(objectMapper.writeValueAsString(unread));
        conversationRepository.save(conv);

        return ResponseEntity.ok(toMessageMap(saved));
    }

    @PatchMapping("/messages/{id}")
    public ResponseEntity<Map<String, Object>> updateMessage(@PathVariable Long id,
                                                              @RequestBody Map<String, Object> body) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message non trouvé"));
        if (body.containsKey("isRead")) msg.setIsRead((Boolean) body.get("isRead"));
        return ResponseEntity.ok(toMessageMap(messageRepository.save(msg)));
    }

    // ── CONVERSATIONS ────────────────────────────────────────────────────────

    @GetMapping("/conversations")
    public ResponseEntity<List<Map<String, Object>>> getConversations() {
        return ResponseEntity.ok(conversationRepository.findAll().stream()
                .map(this::toConvMap).toList());
    }

    @PostMapping("/conversations")
    public ResponseEntity<Map<String, Object>> createConversation(@RequestBody Map<String, Object> body) throws Exception {
        List<Long> ids = ((List<?>) body.get("participantIds")).stream()
                .map(o -> toLong(o)).toList();

        // Vérifier si la conversation existe déjà
        if (ids.size() == 2) {
            Optional<Conversation> existing = conversationRepository.findByTwoParticipants(ids.get(0), ids.get(1));
            if (existing.isPresent()) return ResponseEntity.ok(toConvMap(existing.get()));
        }

        Conversation conv = new Conversation();
        conv.setParticipantIds(new ArrayList<>(ids));
        conv.setParticipantNames(objectMapper.writeValueAsString(body.get("participantNames")));
        conv.setLastMessage("");
        conv.setLastMessageTime(LocalDateTime.now());
        Map<String, Integer> unread = new HashMap<>();
        ids.forEach(id -> unread.put(String.valueOf(id), 0));
        conv.setUnreadCount(objectMapper.writeValueAsString(unread));

        return ResponseEntity.ok(toConvMap(conversationRepository.save(conv)));
    }

    @PatchMapping("/conversations/{id}")
    public ResponseEntity<Map<String, Object>> updateConversation(@PathVariable Long id,
                                                                   @RequestBody Map<String, Object> body) throws Exception {
        Conversation conv = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation non trouvée"));
        if (body.containsKey("lastMessage"))     conv.setLastMessage((String) body.get("lastMessage"));
        if (body.containsKey("lastMessageTime")) conv.setLastMessageTime(LocalDateTime.now());
        if (body.containsKey("unreadCount"))     conv.setUnreadCount(objectMapper.writeValueAsString(body.get("unreadCount")));
        return ResponseEntity.ok(toConvMap(conversationRepository.save(conv)));
    }

    // ── NOTIFICATIONS ────────────────────────────────────────────────────────

    @GetMapping("/notifications")
    public List<Map<String, Object>> getNotifications(@RequestParam(required = false) Long userId) {
        List<Notification> notifs = userId != null
                ? notificationRepository.findByUserId(userId)
                : notificationRepository.findAll();
        return notifs.stream().map(this::toNotifMap).toList();
    }

    @PostMapping("/notifications")
    public ResponseEntity<Map<String, Object>> createNotification(@RequestBody Map<String, Object> body) {
        Notification n = Notification.builder()
                .userId(toLong(body.get("userId")))
                .text((String) body.get("text"))
                .type((String) body.getOrDefault("type", "info"))
                .read(false).build();
        return ResponseEntity.ok(toNotifMap(notificationRepository.save(n)));
    }

    @PatchMapping("/notifications/{id}")
    public ResponseEntity<Map<String, Object>> updateNotification(@PathVariable Long id,
                                                                   @RequestBody Map<String, Object> body) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée"));
        if (body.containsKey("read")) n.setRead((Boolean) body.get("read"));
        return ResponseEntity.ok(toNotifMap(notificationRepository.save(n)));
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private Map<String, Object> toMessageMap(Message m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("conversationId", m.getConversation().getId());
        map.put("senderId", m.getSenderId());
        map.put("receiverId", m.getReceiverId());
        map.put("senderName", m.getSenderName());
        map.put("receiverName", m.getReceiverName());
        map.put("content", m.getContent());
        map.put("timestamp", m.getTimestamp());
        map.put("isRead", m.getIsRead());
        map.put("fileUrl", m.getFileUrl());
        map.put("fileName", m.getFileName());
        map.put("fileType", m.getFileType());
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toConvMap(Conversation c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("participantIds", c.getParticipantIds());
        try {
            map.put("participantNames", c.getParticipantNames() != null
                    ? objectMapper.readValue(c.getParticipantNames(), Map.class) : Map.of());
            map.put("unreadCount", c.getUnreadCount() != null
                    ? objectMapper.readValue(c.getUnreadCount(), Map.class) : Map.of());
        } catch (Exception e) {
            map.put("participantNames", Map.of());
            map.put("unreadCount", Map.of());
        }
        map.put("lastMessage", c.getLastMessage());
        map.put("lastMessageTime", c.getLastMessageTime());
        return map;
    }

    private Map<String, Object> toNotifMap(Notification n) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", n.getId());
        map.put("userId", n.getUserId());
        map.put("text", n.getText());
        map.put("time", n.getTime());
        map.put("type", n.getType());
        map.put("read", n.getRead());
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> parseUnread(String json) {
        try {
            return json != null ? objectMapper.readValue(json, Map.class) : new HashMap<>();
        } catch (Exception e) { return new HashMap<>(); }
    }

    private Long toLong(Object o) {
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }
}
