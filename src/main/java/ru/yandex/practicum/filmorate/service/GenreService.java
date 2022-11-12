package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Pair> getAllGenres() {
        log.info("возвращен перечень всех жанров");
        return genreStorage.getAllGenres();
    }

    public Pair getGenre(int genreID) {
        log.info("возвращен жанр с ID №{}", genreID);
        return genreStorage.getGenre(genreID);
    }
}
