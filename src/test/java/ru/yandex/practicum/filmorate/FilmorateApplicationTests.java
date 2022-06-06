package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.db.DatabaseFilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.db.DatabaseFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.DatabaseFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.db.DatabaseUserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final DatabaseUserStorage userStorage;
    private final DatabaseFilmStorage filmStorage;
    private final DatabaseFriendshipStorage friendshipStorage;
    private final DatabaseFilmLikeStorage filmLikeStorage;
    private final Mpa mpa = new Mpa(1, "G");
    private final HashSet<Integer> likes = new HashSet<>();
    private final HashSet<Integer> genres = new HashSet<>(Set.of(1, 2));

    @BeforeAll
    static void beforeAll(@Autowired DatabaseUserStorage userStorage,
                          @Autowired DatabaseFilmStorage filmStorage) {
        User user1 = new User(1, "u1@test.ru", "u1", "Test", LocalDate.of(2000, 1, 1));
        User user2 = new User(2, "u2@test.ru", "u2", "Test", LocalDate.of(2000, 1, 1));
        User user3 = new User(3, "u3@test.ru", "u3", "Test", LocalDate.of(2000, 1, 1));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        Mpa mpa = new Mpa(1, "G");
        HashSet<Integer> likes = new HashSet<>();
        HashSet<Integer> genres = new HashSet<>(Set.of(1, 2));
        Film film1 = new Film(1, "F1", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        Film film2 = new Film(2, "F2", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        Film film3 = new Film(3, "F3", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
    }

    @Test
    void getFriendsListByIdUnacceptedFriendship() {
        friendshipStorage.addFriend(1, 2);

        List<User> users1 = userStorage.getFriendsListById(1);
        List<User> users2 = userStorage.getFriendsListById(2);

        assertNotNull(users1);
        assertNotNull(users2);
        assertEquals(1, users1.size());
        assertEquals(0, users2.size());

        friendshipStorage.deleteFriend(1, 2);
    }

    @Test
    void getFriendsListByIdAcceptedFriendship() {
        friendshipStorage.addFriend(1, 2);
        friendshipStorage.addFriend(2, 1);

        List<User> users1 = userStorage.getFriendsListById(1);
        List<User> users2 = userStorage.getFriendsListById(2);

        assertNotNull(users1);
        assertNotNull(users2);
        assertEquals(1, users1.size());
        assertEquals(1, users2.size());

        friendshipStorage.deleteFriend(1, 2);
        friendshipStorage.deleteFriend(2, 1);
    }

    @Test
    void getFriendsListByIdEmpty() {
        List<User> users = userStorage.getFriendsListById(1);

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void getCommonFriends() {
        User user3 = new User(3, "u3@test.ru", "u3", "Test", LocalDate.of(2000, 1, 1));
        List<User> usersExpected = List.of(user3);
        friendshipStorage.addFriend(1, 3);
        friendshipStorage.addFriend(2, 3);

        List<User> users = userStorage.getCommonFriends(1, 2);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(usersExpected, users);

        friendshipStorage.deleteFriend(1, 3);
        friendshipStorage.deleteFriend(2, 3);
    }

    @Test
    void getCommonFriendsEmpty() {
        List<User> users = userStorage.getCommonFriends(1, 2);

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void getUsers() {
        User user1 = new User(1, "u1@test.ru", "u1", "Test", LocalDate.of(2000, 1, 1));
        User user2 = new User(2, "u2@test.ru", "u2", "Test", LocalDate.of(2000, 1, 1));
        User user3 = new User(3, "u3@test.ru", "u3", "Test", LocalDate.of(2000, 1, 1));
        List<User> usersExpected = List.of(user1, user2, user3);

        List<User> users = userStorage.getUsers();

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals(usersExpected, users);
    }

    @Test
    void getUserById() {
        User userExpected = new User(1, "u1@test.ru", "u1", "Test", LocalDate.of(2000, 1, 1));

        User user = userStorage.getUserById(1);

        assertNotNull(user);
        assertEquals(userExpected, user);
    }

    @Test
    void getUserByIdIncorrectId() {
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(-1));
    }

    @Test
    void addUser() {
        User user4 = new User(0, "u4@test.ru", "u4", "Test", LocalDate.of(2000, 1, 1));
        User userExpected = new User(4, "u4@test.ru", "u4", "Test", LocalDate.of(2000, 1, 1));

        User user = userStorage.addUser(user4);

        assertNotNull(user);
        assertEquals(userExpected, user);

        userStorage.deleteUserById(4);
    }

    @Test
    void updateUser() {
        User user5 = new User(5, "u5@test.ru", "u5", "Test", LocalDate.of(2000, 1, 1));
        User userUpd = new User(5, "upd@test.ru", "upd", "UPD", LocalDate.of(1900, 1, 1));
        userStorage.addUser(user5);

        User user = userStorage.updateUser(userUpd);

        assertNotNull(user);
        assertEquals(userUpd, user);

        userStorage.deleteUserById(5);
    }

    @Test
    void deleteUserById() {
        User userDel = new User(6, "del@test.ru", "del", "DEL", LocalDate.of(2000, 1, 1));
        userStorage.addUser(userDel);

        userStorage.deleteUserById(6);

        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(6));
    }

    @Test
    void getPopularFilms() {
        HashSet<Integer> likes2 = new HashSet<>(Set.of(1, 2, 3));
        HashSet<Integer> likes3 = new HashSet<>(Set.of(1, 2));
        Film film1 = new Film(1, "F1", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        Film film2 = new Film(2, "F2", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes2, genres);
        Film film3 = new Film(3, "F3", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes3, genres);
        List<Film> filmsExpected = List.of(film2, film3, film1);
        filmLikeStorage.addLike(2, 1);
        filmLikeStorage.addLike(2, 2);
        filmLikeStorage.addLike(2, 3);
        filmLikeStorage.addLike(3, 1);
        filmLikeStorage.addLike(3, 2);


        List<Film> films = filmStorage.getPopularFilms(5);

        assertNotNull(films);
        assertEquals(3, films.size());
        assertEquals(filmsExpected, films);

        filmLikeStorage.deleteLike(2, 1);
        filmLikeStorage.deleteLike(2, 2);
        filmLikeStorage.deleteLike(2, 3);
        filmLikeStorage.deleteLike(3, 1);
        filmLikeStorage.deleteLike(3, 2);
    }

    @Test
    void getPopularFilmsCount1() {
        HashSet<Integer> likes2 = new HashSet<>(Set.of(1, 2));
        Film film2 = new Film(2, "F2", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes2, genres);
        List<Film> filmsExpected = List.of(film2);
        filmLikeStorage.addLike(2, 1);
        filmLikeStorage.addLike(2, 2);
        filmLikeStorage.addLike(3, 1);

        List<Film> films = filmStorage.getPopularFilms(1);

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(filmsExpected, films);

        filmLikeStorage.deleteLike(2, 1);
        filmLikeStorage.deleteLike(2, 2);
        filmLikeStorage.deleteLike(3, 1);
    }

    @Test
    void getPopularFilmsNoLikes() {
        List<Film> films = filmStorage.getPopularFilms(5);

        assertNotNull(films);
        assertEquals(3, films.size());
    }

    @Test
    void getFilms() {
        Film film1 = new Film(1, "F1", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        Film film2 = new Film(2, "F2", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        Film film3 = new Film(3, "F3", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        List<Film> filmsExpected = List.of(film1, film2, film3);

        List<Film> films = filmStorage.getFilms();

        assertNotNull(films);
        assertEquals(3, films.size());
        assertEquals(filmsExpected, films);
    }

    @Test
    void getFilmById() {
        Film filmExpected = new Film(1, "F1", "Test", LocalDate.of(2000, 1, 1), 100,
                mpa, likes, genres);

        Film film = filmStorage.getFilmById(1);

        assertNotNull(film);
        assertEquals(filmExpected, film);
    }

    @Test
    void getFilmByIdIncorrectId() {
        assertThrows(FilmNotFoundException.class, () -> filmStorage.getFilmById(-1));
    }

    @Test
    void addFilm() {
        Film film4 = new Film(0, "F4", "Test", LocalDate.of(2000, 1, 1), 100,
                mpa, likes, genres);
        Film filmExpected = new Film(4, "F4", "Test", LocalDate.of(2000, 1, 1), 100,
                mpa, likes, genres);

        Film film = filmStorage.addFilm(film4);

        assertNotNull(film);
        assertEquals(filmExpected, film);

        filmStorage.deleteFilmById(4);
    }

    @Test
    void updateFilm() {
        HashSet<Integer> upd = new HashSet<>(Set.of(2, 3));
        Film film5 = new Film(5, "F5", "Test", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        Film filmUpd = new Film(5, "UPD", "UPD", LocalDate.of(1900, 1, 1), 200, mpa, likes, upd);
        filmStorage.addFilm(film5);

        Film film = filmStorage.updateFilm(filmUpd);

        assertNotNull(film);
        assertEquals(filmUpd, film);

        filmStorage.deleteFilmById(5);
    }

    @Test
    void deleteFilmById() {
        Film filmDel = new Film(6, "DEL", "", LocalDate.of(2000, 1, 1), 100, mpa, likes, genres);
        filmStorage.addFilm(filmDel);

        filmStorage.deleteFilmById(6);

        assertThrows(FilmNotFoundException.class, () -> filmStorage.getFilmById(6));
    }
}
