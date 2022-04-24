package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Getter
@Setter
@EqualsAndHashCode
public class Film {
    private int id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Size(max=200)
    private String description;
    @NotNull
    @Min(value = MIN_RELEASE_DATE, message = "должно быть не раньше 28 декабря 1895 года")
    private long releaseDate;
    @Positive
    private int duration;

    private static final long MIN_RELEASE_DATE = -2335564800000L;

    public Film(int id, String name, String description, String releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate)
                .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        this.duration = duration;
    }
}
