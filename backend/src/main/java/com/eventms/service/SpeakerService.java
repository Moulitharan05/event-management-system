package com.eventms.service;

import com.eventms.dto.SpeakerRequest;
import com.eventms.exception.ResourceNotFoundException;
import com.eventms.model.Speaker;
import com.eventms.repository.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }

    public Speaker getSpeakerById(Long id) {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speaker not found with id: " + id));
    }

    public Speaker createSpeaker(SpeakerRequest request) {
        Speaker speaker = Speaker.builder()
                .name(request.getName())
                .bio(request.getBio())
                .company(request.getCompany())
                .build();
        return speakerRepository.save(speaker);
    }

    public Speaker updateSpeaker(Long id, SpeakerRequest request) {
        Speaker speaker = getSpeakerById(id);
        speaker.setName(request.getName());
        speaker.setBio(request.getBio());
        speaker.setCompany(request.getCompany());
        return speakerRepository.save(speaker);
    }

    public void deleteSpeaker(Long id) {
        Speaker speaker = getSpeakerById(id);
        speakerRepository.delete(speaker);
    }
}
