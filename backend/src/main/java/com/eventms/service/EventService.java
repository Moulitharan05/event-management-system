package com.eventms.service;

import com.eventms.dto.EventRequest;
import com.eventms.dto.EventResponse;
import com.eventms.exception.ResourceNotFoundException;
import com.eventms.model.Event;
import com.eventms.model.Speaker;
import com.eventms.repository.EventRepository;
import com.eventms.repository.RegistrationRepository;
import com.eventms.repository.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;
    private final RegistrationRepository registrationRepository;

    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<EventResponse> searchEvents(String keyword, String category, String venue,
                                             LocalDateTime fromDate, LocalDateTime toDate) {
        return eventRepository.search(blankToNull(keyword), blankToNull(category), blankToNull(venue), fromDate, toDate)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        return toResponse(getEventEntity(id));
    }

    public Event getEventEntity(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    public EventResponse createEvent(EventRequest request) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .venue(request.getVenue())
                .category(request.getCategory())
                .capacity(request.getCapacity() != null ? request.getCapacity() : 100)
                .speakers(resolveSpeakers(request.getSpeakerIds()))
                .build();
        return toResponse(eventRepository.save(event));
    }

    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = getEventEntity(id);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setVenue(request.getVenue());
        event.setCategory(request.getCategory());
        if (request.getCapacity() != null) {
            event.setCapacity(request.getCapacity());
        }
        event.setSpeakers(resolveSpeakers(request.getSpeakerIds()));
        return toResponse(eventRepository.save(event));
    }

    public void deleteEvent(Long id) {
        Event event = getEventEntity(id);
        eventRepository.delete(event);
    }

    private Set<Speaker> resolveSpeakers(List<Long> speakerIds) {
        if (speakerIds == null || speakerIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(speakerRepository.findAllById(speakerIds));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private EventResponse toResponse(Event event) {
        List<EventResponse.SpeakerDto> speakerDtos = event.getSpeakers().stream()
                .map(s -> EventResponse.SpeakerDto.builder()
                        .id(s.getId()).name(s.getName()).bio(s.getBio()).company(s.getCompany())
                        .build())
                .collect(Collectors.toList());

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .venue(event.getVenue())
                .category(event.getCategory())
                .capacity(event.getCapacity())
                .registeredCount(registrationRepository.countByEvent(event))
                .speakers(speakerDtos)
                .build();
    }
}
