package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode
public class Genre {
    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
