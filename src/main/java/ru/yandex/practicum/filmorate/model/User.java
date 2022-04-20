package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private Instant birthday;

    public User(int id, String email, String login, String name, String birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = LocalDate.parse(birthday).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
