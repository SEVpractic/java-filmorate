package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
class BdClassTests {
    protected static final LocalDate CORRECT_DATE = LocalDate.of(1990, 10, 3);
    protected static final Duration CORRECT_DURATION = Duration.ofMinutes(120);
    private static JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void createTests() {
        jdbcTemplate = new JdbcTemplate(BdClassTests.mysqlDataSource());
    }

    @BeforeEach
    public void createTables() {
        jdbcTemplate.update("CREATE table IF NOT EXISTS users(" +
                "user_id int generated by default as identity primary key," +
                "email varchar NOT NULL," +
                "login varchar NOT NULL," +
                "name varchar," +
                "birthday date)"
        );
        jdbcTemplate.update("CREATE unique index if not exists user_email_uindex ON users (email)");
        jdbcTemplate.update("CREATE unique index if not exists user_login_uindex ON users (login)");
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS friends(" +
                "user_id int REFERENCES users ON DELETE CASCADE," +
                "friend_id int REFERENCES users ON DELETE CASCADE," +
                "is_confirmed boolean," +
                "PRIMARY KEY (user_id, friend_id))"
        );
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS mpa(" +
                "mpa_id int generated by default as identity primary key," +
                "mpa_name varchar)"
        );
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS films(" +
                "    film_id int generated by default as identity primary key," +
                "    name varchar NOT NULL," +
                "    description varchar(200)," +
                "    release_date date NOT NULL," +
                "    duration int NOT NULL," +
                "    rate int," +
                "    mpa_id int REFERENCES mpa (mpa_id) ON DELETE CASCADE)"
        );
        jdbcTemplate.update("CREATE unique index if not exists film_name_uindex ON films(name)");
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS genres(" +
                "genre_id int generated by default as identity primary key," +
                "genre_name varchar)"
        );
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS genres_film(" +
                "film_id int REFERENCES films (film_id) ON DELETE CASCADE," +
                "genre_id int REFERENCES genres (genre_id) ON DELETE CASCADE," +
                "PRIMARY KEY (film_id, genre_id))"
        );
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS likes(" +
                "film_id int REFERENCES films ON DELETE CASCADE," +
                "user_id int REFERENCES users ON DELETE CASCADE," +
                "PRIMARY KEY (film_id, user_id))"
        );
        jdbcTemplate.update("INSERT INTO genres (genre_name) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик')");
        jdbcTemplate.update("INSERT INTO mpa (mpa_name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17')");
    }

    @AfterEach
    public void dropTables(){
        String sqlQuery = "DROP TABLE IF EXISTS friends, users, genres_film, genres, mpa, films, likes";
        jdbcTemplate.update(sqlQuery);
    }

    public static DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:file:./db/filmorate");
        dataSource.setUsername("sa");
        dataSource.setPassword("password");
        return dataSource;
    }
}
