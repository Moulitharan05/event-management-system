package com.eventms.service;

import com.eventms.dto.EventRequest;
import com.eventms.dto.EventResponse;
import com.eventms.exception.ResourceNotFoundException;
import com.eventms.model.Event;
import com.eventms.repository.EventRepository;
import com.eventms.repository.RegistrationRepository;
import com.eventms.repository.SpeakerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private SpeakerRepository speakerRepository;
    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private EventService eventService;

    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = Event.builder()
                .id(1L)
                .title("Spring Boot Meetup")
                .description("A meetup about Spring Boot")
                .eventDate(LocalDateTime.now().plusDays(5))
                .venue("Tech Hub")
                .category("Technology")
                .capacity(50)
                .build();
    }

    @Test
    void getEventById_returnsEvent_whenFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(registrationRepository.countByEvent(sampleEvent)).thenReturn(3L);

        EventResponse response = eventService.getEventById(1L);

        assertThat(response.getTitle()).isEqualTo("Spring Boot Meetup");
        assertThat(response.getRegisteredCount()).isEqualTo(3L);
    }

    @Test
    void getEventById_throws_whenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createEvent_savesAndReturnsMappedResponse() {
        EventRequest request = new EventRequest();
        request.setTitle("New Event");
        request.setDescription("Description");
        request.setEventDate(LocalDateTime.now().plusDays(10));
        request.setVenue("Main Hall");
        request.setCategory("Workshop");
        request.setCapacity(30);

        Event saved = Event.builder()
                .id(2L)
                .title("New Event")
                .description("Description")
                .eventDate(request.getEventDate())
                .venue("Main Hall")
                .category("Workshop")
                .capacity(30)
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(saved);
        when(registrationRepository.countByEvent(saved)).thenReturn(0L);

        EventResponse response = eventService.createEvent(request);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getTitle()).isEqualTo("New Event");
        assertThat(response.getCapacity()).isEqualTo(30);
    }
}
