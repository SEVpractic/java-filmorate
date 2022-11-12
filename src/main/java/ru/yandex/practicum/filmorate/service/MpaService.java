package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Pair> getAllMpa() {
        log.info("возвращен перечень всех МПА");
        return mpaDbStorage.getAllMpa();
    }

    public Pair getMpa(int mpaID) {
        log.info("возвращен MPA с ID №{}", mpaID);
        return mpaDbStorage.getMpa(mpaID);
    }
}
