package com.skillink.backend.controller;

import com.skillink.backend.dto.ReviewDto;
import com.skillink.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewDto> getAll(@RequestParam(required = false) Long prestataireId,
                                   @RequestParam(required = false) Long clientId) {
        if (prestataireId != null) return reviewService.getByPrestataire(prestataireId);
        if (clientId != null)      return reviewService.getByClient(clientId);
        return reviewService.getAll();
    }

    @PostMapping
    public ResponseEntity<ReviewDto> create(@RequestBody Map<String, Object> body,
                                             @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(reviewService.create(body, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
