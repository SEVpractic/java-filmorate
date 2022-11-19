package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Pair> getAll() {
        List<Pair> mpa = mpaStorage.getAll();
        log.info("возвращен перечень всех МПА");
        return mpa;
    }

    public Pair get(int mpaID) {
        Pair mpa = mpaStorage.get(mpaID);
        log.info("возвращен MPA с ID №{}", mpaID);
        return mpa;
    }
}
