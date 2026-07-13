package com.eventms.controller;

import com.eventms.dto.EventResponse;
import com.eventms.security.JwtAuthFilter;
import com.eventms.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser
    void getUpcomingEvents_returnsList() throws Exception {
        EventResponse response = EventResponse.builder()
                .id(1L).title("Tech Talk").venue("Auditorium")
                .eventDate(LocalDateTime.now().plusDays(1))
                .registeredCount(5)
                .build();

        when(eventService.getUpcomingEvents()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tech Talk"))
                .andExpect(jsonPath("$[0].registeredCount").value(5));
    }

    @Test
    @WithMockUser
    void getEvent_returnsSingleEvent() throws Exception {
        EventResponse response = EventResponse.builder().id(1L).title("Tech Talk").build();
        when(eventService.getEventById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
