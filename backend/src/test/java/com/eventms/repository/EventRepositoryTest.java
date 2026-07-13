package com.eventms.repository;

import com.eventms.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void findByEventDateAfter_returnsOnlyFutureEvents() {
        Event past = Event.builder()
                .title("Past Event").venue("Hall A")
                .eventDate(LocalDateTime.now().minusDays(2))
                .build();
        Event future = Event.builder()
                .title("Future Event").venue("Hall B")
                .eventDate(LocalDateTime.now().plusDays(2))
                .build();
        eventRepository.save(past);
        eventRepository.save(future);

        List<Event> results = eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Future Event");
    }

    @Test
    void search_filtersByKeywordAndCategory() {
        Event event = Event.builder()
                .title("Java Conference").venue("Convention Center")
                .description("All about Java").category("Technology")
                .eventDate(LocalDateTime.now().plusDays(3))
                .build();
        eventRepository.save(event);

        List<Event> results = eventRepository.search("java", "Technology", null, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Java Conference");
    }
}
