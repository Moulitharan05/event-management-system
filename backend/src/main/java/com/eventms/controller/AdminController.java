package com.eventms.controller;

import com.eventms.dto.EventRequest;
import com.eventms.dto.EventResponse;
import com.eventms.dto.RegistrationResponse;
import com.eventms.dto.SpeakerRequest;
import com.eventms.model.Speaker;
import com.eventms.service.EventService;
import com.eventms.service.RegistrationService;
import com.eventms.service.SpeakerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final SpeakerService speakerService;
    private final RegistrationService registrationService;

    // ---- Event management ----
    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Speaker management ----
    @PostMapping("/speakers")
    public ResponseEntity<Speaker> createSpeaker(@Valid @RequestBody SpeakerRequest request) {
        return ResponseEntity.ok(speakerService.createSpeaker(request));
    }

    @PutMapping("/speakers/{id}")
    public ResponseEntity<Speaker> updateSpeaker(@PathVariable Long id, @Valid @RequestBody SpeakerRequest request) {
        return ResponseEntity.ok(speakerService.updateSpeaker(id, request));
    }

    @DeleteMapping("/speakers/{id}")
    public ResponseEntity<Void> deleteSpeaker(@PathVariable Long id) {
        speakerService.deleteSpeaker(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Attendance tracking ----
    @GetMapping("/events/{eventId}/attendees")
    public ResponseEntity<List<RegistrationResponse>> getAttendees(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getEventAttendees(eventId));
    }

    @PatchMapping("/registrations/{registrationId}/attendance")
    public ResponseEntity<RegistrationResponse> markAttendance(@PathVariable Long registrationId,
                                                                 @RequestParam boolean attended) {
        return ResponseEntity.ok(registrationService.markAttendance(registrationId, attended));
    }
}
