package com.skillink.backend.controller;

import com.skillink.backend.dto.ServiceDto;
import com.skillink.backend.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public List<ServiceDto> getAll() {
        return serviceService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getById(id));
    }

    @GetMapping("/mine")
    public List<ServiceDto> getMine(@AuthenticationPrincipal UserDetails user) {
        return serviceService.getMine(user.getUsername());
    }

    @PostMapping
    public ResponseEntity<ServiceDto> create(@RequestBody Map<String, Object> body,
                                              @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(serviceService.create(body, user.getUsername()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceDto> update(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(serviceService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
