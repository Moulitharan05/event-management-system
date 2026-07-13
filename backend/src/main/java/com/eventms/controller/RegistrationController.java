package com.eventms.controller;

import com.eventms.dto.RegistrationResponse;
import com.eventms.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/events/{eventId}")
    public ResponseEntity<RegistrationResponse> register(@AuthenticationPrincipal UserDetails principal,
                                                           @PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.registerForEvent(principal.getUsername(), eventId));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal UserDetails principal,
                                        @PathVariable Long eventId) {
        registrationService.cancelRegistration(principal.getUsername(), eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<RegistrationResponse>> myRegistrations(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(registrationService.getMyRegistrations(principal.getUsername()));
    }
}
