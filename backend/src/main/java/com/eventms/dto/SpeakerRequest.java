package com.eventms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpeakerRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String bio;
    private String company;
}
