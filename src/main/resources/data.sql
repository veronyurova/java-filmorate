MERGE INTO mpa (id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

MERGE INTO genre (id, name)
VALUES (1, 'Драма'),
       (2, 'Комедия'),
       (3, 'Триллер'),
       (4, 'Фантастика'),
       (5, 'Фэнтези'),
       (6, 'Мультфильм'),
       (7, 'Документальный');