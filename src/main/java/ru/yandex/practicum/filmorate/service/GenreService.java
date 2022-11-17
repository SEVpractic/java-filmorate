package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Pair> getAllGenres() {
        List<Pair> genres = genreStorage.getAllGenres();
        log.info("возвращен перечень всех жанров");
        return genres;
    }

    public Pair getGenre(int genreID) {
        Pair genre = genreStorage.getGenre(genreID);
        log.info("возвращен жанр с ID №{}", genreID);
        return genre;
    }
}
