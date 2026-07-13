package com.eventms.service;

import com.eventms.model.Event;
import com.eventms.model.Registration;
import com.eventms.repository.EventRepository;
import com.eventms.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Sends a reminder email to registered users roughly 24 hours before an event starts.
 * Runs every hour; relies on the event window (23-24h out) so each event is only
 * caught once as the scheduler ticks forward.
 */
@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 * * * *") // once every hour, on the hour
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(23);
        LocalDateTime windowEnd = now.plusHours(24);

        List<Event> upcoming = eventRepository.findByEventDateAfterOrderByEventDateAsc(now);
        for (Event event : upcoming) {
            if (event.getEventDate().isAfter(windowStart) && event.getEventDate().isBefore(windowEnd)) {
                List<Registration> registrations = registrationRepository.findByEvent(event);
                for (Registration registration : registrations) {
                    emailService.sendEventReminder(registration.getUser(), event);
                }
            }
        }
    }
}
