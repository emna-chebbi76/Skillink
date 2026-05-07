package com.skillink.backend.controller;

import com.skillink.backend.dto.BookingDto;
import com.skillink.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAll() {
        return bookingService.getAll();
    }

    @GetMapping("/mine")
    public List<BookingDto> getMine(@AuthenticationPrincipal UserDetails user) {
        return bookingService.getMine(user.getUsername());
    }

    @GetMapping("/provider")
    public List<BookingDto> getForProvider(@AuthenticationPrincipal UserDetails user) {
        return bookingService.getForProvider(user.getUsername());
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody Map<String, Object> body,
                                              @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(bookingService.create(body, user.getUsername()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingDto> update(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(bookingService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
