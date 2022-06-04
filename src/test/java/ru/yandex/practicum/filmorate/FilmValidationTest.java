package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@JsonTest
class FilmValidationTest {
    @Autowired
    private JacksonTester<Film> jsonTester;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public List<String> validateFilm(Film film) {
        return validator.validate(film).stream()
                                       .map(ConstraintViolation::getMessage)
                                       .collect(Collectors.toList());
    }

    @Test
    void allFieldsAreCorrectShouldSucceed() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(0, violations.size());
        assertEquals("Stranger Things", film.getName());
        assertEquals("Description", film.getDescription());
        assertEquals("2016-07-15", film.getReleaseDate().toString());
        assertEquals(50, film.getDuration());
    }

    @Test
    void emptyNameShouldFail() throws IOException {
        String json = "{\"name\": \"\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
    }

    @Test
    void blankNameShouldFail() throws IOException {
        String json = "{\"name\": \"     \", \"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
    }

    @Test
    void nullNameShouldFail() throws IOException {
        String json = "{\"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("не должно равняться null"));
    }

    @Test
    void emptyDescriptionShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
    }

    @Test
    void blankDescriptionShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"     \", " +
                "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
    }

    @Test
    void descriptionLength200ShouldSucceed() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": " +
                      "\"descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                      "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                      "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                      "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(0, violations.size());
        assertEquals("descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                     "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                     "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                     "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr",
                     film.getDescription());
    }

    @Test
    void descriptionLength201ShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": " +
                      "\"descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                      "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                      "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr" +
                      "descrdescrdescrdescrdescrdescrdescrdescrdescrdescr1\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("размер должен находиться в диапазоне от 0 до 200"));
    }

    @Test
    void nullDescriptionShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("не должно равняться null"));
    }

    @Test
    void correctReleaseDateShouldSucceed() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"1895-12-28\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(0, violations.size());
        assertEquals("1895-12-28", film.getReleaseDate().toString());
    }

    @Test
    void incorrectReleaseDateShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"1895-12-27\", \"duration\": 50, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно быть не раньше 28 декабря 1895 года"));
    }

    @Test
    void positiveDurationShouldSucceed() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 100, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(0, violations.size());
        assertEquals(100, film.getDuration());
    }

    @Test
    void negativeDurationShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": -1, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно быть больше 0"));
    }

    @Test
    void zeroDurationShouldFail() throws IOException {
        String json = "{\"name\": \"Stranger Things\", \"description\": \"Description\", " +
                      "\"releaseDate\": \"2016-07-15\", \"duration\": 0, \"mpa\": { \"id\": 1}}";

        Film film = jsonTester.parse(json).getObject();
        List<String> violations = validateFilm(film);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно быть больше 0"));
    }
}
