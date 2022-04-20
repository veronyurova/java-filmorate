package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Film {
    private int id;
    private String name;
    private String description;
    private Instant releaseDate;
    private int duration;

    public Film(int id, String name, String description, String releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate).atStartOfDay().toInstant(ZoneOffset.UTC);
        this.duration = duration;
    }
}
