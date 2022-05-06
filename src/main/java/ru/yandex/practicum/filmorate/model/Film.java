package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.constraints.MinDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@EqualsAndHashCode
public class Film {
    private int id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    @MinDate(date = "1895-12-28", message = "должно быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private final Set<Integer> likes;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likes = new HashSet<>();
    }
}
