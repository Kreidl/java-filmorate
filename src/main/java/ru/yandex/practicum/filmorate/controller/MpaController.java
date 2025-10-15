package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exceptions.ErrorHandler;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Import(ErrorHandler.class)
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<MpaDto> getAllMpaRatings() {
        log.info("Запрос на получение всех рейтингов Mpa фильмов.");
        return mpaService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaDto getMpaRatingById(@PathVariable int id) {
        log.info("Запрос на получение рейтинга Mpa фильма с id={}.", id);
        return mpaService.getMpaRatingById(id);
    }
}
