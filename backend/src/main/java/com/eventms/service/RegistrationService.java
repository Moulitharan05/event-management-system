package com.eventms.service;

import com.eventms.dto.RegistrationResponse;
import com.eventms.exception.BadRequestException;
import com.eventms.exception.ResourceNotFoundException;
import com.eventms.model.Event;
import com.eventms.model.Registration;
import com.eventms.model.User;
import com.eventms.repository.EventRepository;
import com.eventms.repository.RegistrationRepository;
import com.eventms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RegistrationResponse registerForEvent(String userEmail, Long eventId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new BadRequestException("You are already registered for this event");
        }

        long currentCount = registrationRepository.countByEvent(event);
        if (event.getCapacity() != null && currentCount >= event.getCapacity()) {
            throw new BadRequestException("This event is full");
        }

        Registration registration = Registration.builder()
                .user(user)
                .event(event)
                .build();
        registration = registrationRepository.save(registration);

        emailService.sendRegistrationConfirmation(user, event);

        return toResponse(registration);
    }

    public void cancelRegistration(String userEmail, Long eventId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        Registration registration = registrationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registrationRepository.delete(registration);
    }

    public List<RegistrationResponse> getMyRegistrations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return registrationRepository.findByUser(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<RegistrationResponse> getEventAttendees(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return registrationRepository.findByEvent(event).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public RegistrationResponse markAttendance(Long registrationId, boolean attended) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registration.setAttended(attended);
        return toResponse(registrationRepository.save(registration));
    }

    private RegistrationResponse toResponse(Registration registration) {
        Event event = registration.getEvent();
        User user = registration.getUser();
        return RegistrationResponse.builder()
                .id(registration.getId())
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventDate(event.getEventDate())
                .venue(event.getVenue())
                .attended(registration.isAttended())
                .registeredAt(registration.getRegisteredAt())
                .userFullName(user.getFullName())
                .userEmail(user.getEmail())
                .build();
    }
}
