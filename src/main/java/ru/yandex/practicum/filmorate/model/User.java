package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Getter
@Setter
@EqualsAndHashCode
public class User {
    private int id;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z]+$",
             message = "должно содержать только латинские буквы без пробелов")
    private String login;
    @NotNull
    private String name;
    @NotNull
    @Past
    private Instant birthday;

    public User(int id, String email, String login, String name, String birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = LocalDate.parse(birthday).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
