package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Pair> getAll() {
        return mpaService.getAll();
    }

    @GetMapping(path = "/{id}")
    public Pair get(@PathVariable("id") int mpaID) {
        return mpaService.get(mpaID);
    }
}
