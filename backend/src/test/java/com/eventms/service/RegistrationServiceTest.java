package com.eventms.service;

import com.eventms.exception.BadRequestException;
import com.eventms.model.Event;
import com.eventms.model.User;
import com.eventms.repository.EventRepository;
import com.eventms.repository.RegistrationRepository;
import com.eventms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegistrationService registrationService;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").fullName("Test User").build();
        event = Event.builder().id(1L).title("Test Event").capacity(2).build();
    }

    @Test
    void registerForEvent_throws_whenAlreadyRegistered() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserAndEvent(user, event)).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerForEvent("user@test.com", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void registerForEvent_throws_whenEventFull() {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(registrationRepository.existsByUserAndEvent(user, event)).thenReturn(false);
        when(registrationRepository.countByEvent(event)).thenReturn(2L);

        assertThatThrownBy(() -> registrationService.registerForEvent("user@test.com", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("full");
    }
}
