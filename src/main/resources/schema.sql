DROP TABLE IF EXISTS friends, users, genres_film, genres, mpa, films, likes;

CREATE table IF NOT EXISTS users(
    user_id int generated by default as identity primary key,
    email varchar NOT NULL,
    login varchar NOT NULL,
    name varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS friends(
    user_id int REFERENCES users ON DELETE CASCADE,
    friend_id int REFERENCES users ON DELETE CASCADE,
    is_confirmed boolean
);

CREATE TABLE IF NOT EXISTS mpa(
    mpa_id int generated by default as identity primary key,
    name varchar
);

CREATE TABLE IF NOT EXISTS films(
    film_id int generated by default as identity primary key,
    name varchar NOT NULL,
    description varchar(200),
    release_date date NOT NULL,
    duration int NOT NULL,
    rate int,
    mpa_id int REFERENCES mpa (mpa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres(
    genre_id int generated by default as identity primary key,
    name varchar
);

CREATE TABLE IF NOT EXISTS genres_film(
    film_id int REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id int REFERENCES genres (genre_id) ON DELETE CASCADE
);

INSERT INTO genres (name)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

INSERT INTO mpa (name)
VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

CREATE TABLE IF NOT EXISTS likes(
    film_id int REFERENCES films (film_id) ON DELETE CASCADE,
    user_id int REFERENCES users (user_id) ON DELETE CASCADE
);
