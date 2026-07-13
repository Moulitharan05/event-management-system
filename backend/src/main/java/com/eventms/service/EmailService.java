package com.eventms.service;

import com.eventms.model.Event;
import com.eventms.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy 'at' hh:mm a");

    @Async
    public void sendRegistrationConfirmation(User user, Event event) {
        String subject = "Registration Confirmed: " + event.getTitle();
        String body = String.format(
                "Hi %s,%n%nYou're confirmed for \"%s\".%n%nDate: %s%nVenue: %s%n%nWe look forward to seeing you there!%n%n- Event Management System",
                user.getFullName(), event.getTitle(), event.getEventDate().format(FORMATTER), event.getVenue());
        send(user.getEmail(), subject, body);
    }

    @Async
    public void sendEventReminder(User user, Event event) {
        String subject = "Reminder: " + event.getTitle() + " is coming up";
        String body = String.format(
                "Hi %s,%n%nThis is a reminder that \"%s\" is happening soon.%n%nDate: %s%nVenue: %s%n%nSee you there!%n%n- Event Management System",
                user.getFullName(), event.getTitle(), event.getEventDate().format(FORMATTER), event.getVenue());
        send(user.getEmail(), subject, body);
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
