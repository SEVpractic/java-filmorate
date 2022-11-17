package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public List<Pair> getAllMpa() {
        List<Pair> mpa = mpaDbStorage.getAllMpa();
        log.info("возвращен перечень всех МПА");
        return mpa;
    }

    public Pair getMpa(int mpaID) {
        Pair mpa = mpaDbStorage.getMpa(mpaID);
        log.info("возвращен MPA с ID №{}", mpaID);
        return mpa;
    }
}
