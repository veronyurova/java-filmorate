package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@JsonTest
class UserValidationTest {
    @Autowired
    private JacksonTester<User> jsonTester;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public List<String> validateUser(User user) {
        return validator.validate(user).stream()
                                       .map(ConstraintViolation::getMessage)
                                       .collect(Collectors.toList());
    }

    @Test
    void allFieldsAreCorrectShouldSucceed() throws IOException {
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(0, violations.size());
        assertEquals("twinpeaks", user.getLogin());
        assertEquals("David Linch", user.getName());
        assertEquals("mail@mail.ru", user.getEmail());
        assertEquals("1946-01-20", user.getBirthday().toString());
    }

    @Test
    void emptyLoginShouldFail() throws IOException {
        String json = "{\"login\": \"\", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("должно содержать только латинские буквы без пробелов"));
    }

    @Test
    void blankLoginShouldFail() throws IOException {
        String json = "{\"login\": \"     \", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("должно содержать только латинские буквы без пробелов"));
    }

    @Test
    void nullLoginShouldFail() throws IOException {
        String json = "{\"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("не должно равняться null"));
    }

    @Test
    void loginWithSpacesShouldFail() throws IOException {
        String json = "{\"login\": \" Twin Peaks \", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно содержать только латинские буквы без пробелов"));
    }

    @Test
    void loginWithNumbersAndSpecialSymbolsShouldFail() throws IOException {
        String json = "{\"login\": \"TwinPeaks23!#$\", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно содержать только латинские буквы без пробелов"));
    }

    @Test
    void loginWithLargeLettersShouldSucceed() throws IOException {
        String json = "{\"login\": \"TwinPeaks\", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(0, violations.size());
        assertEquals("TwinPeaks", user.getLogin());
    }

    @Test
    void nameWithSpacesNumbersAndSpecialSymbolsShouldSucceed() throws IOException {
        String json = "{\"login\": \"twinpeaks\", " +
                "\"name\": \"Волан-де-Морт - нехороший человек!\", " +
                "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(0, violations.size());
        assertEquals("Волан-де-Морт - нехороший человек!", user.getName());
    }

    @Test
    void emptyNameShouldSucceed() throws IOException {
        String json = "{\"login\": \"twinpeaks\", \"name\": \"\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(0, violations.size());
        assertEquals("", user.getName());
    }

    @Test
    void nullNameShouldFail() throws IOException {
        String json = "{\"login\": \"twinpeaks\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("не должно равняться null"));
    }

    @Test
    void emptyEmailShouldFail() throws IOException {
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                      "\"email\": \"\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
    }

    @Test
    void blankEmailShouldFail() throws IOException {
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                      "\"email\": \"     \", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("должно иметь формат адреса электронной почты"));
    }

    @Test
    void nullEmailShouldFail() throws IOException {
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                      "\"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(2, violations.size());
        assertTrue(violations.contains("не должно быть пустым"));
        assertTrue(violations.contains("не должно равняться null"));
    }

    @Test
    void emailWithIncorrectFormatShouldFail() throws IOException {
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                "\"email\": \"mail.mail@\", \"birthday\": \"1946-01-20\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно иметь формат адреса электронной почты"));
    }

    @Test
    void birthdayYesterdayShouldSucceed() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                       .withZone(ZoneOffset.UTC);
        Instant yesterday = Instant.now().minusSeconds(86400);
        String birthday = formatter.format(yesterday);
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"" + birthday + "\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(0, violations.size());
    }

    @Test
    void birthdayTomorrowShouldFail() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                       .withZone(ZoneOffset.UTC);
        Instant now = Instant.now().plusSeconds(86400);
        String birthday = formatter.format(now);
        String json = "{\"login\": \"twinpeaks\", \"name\": \"David Linch\", " +
                      "\"email\": \"mail@mail.ru\", \"birthday\": \"" + birthday + "\"}";

        User user = jsonTester.parse(json).getObject();
        List<String> violations = validateUser(user);

        assertEquals(1, violations.size());
        assertTrue(violations.contains("должно содержать прошедшую дату"));
    }
}