package com.eventms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "speakers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String bio;

    private String company;

    @ManyToMany(mappedBy = "speakers")
    @JsonIgnore
    @Builder.Default
    private Set<Event> events = new HashSet<>();
}
