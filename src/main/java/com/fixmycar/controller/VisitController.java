package com.fixmycar.controller;

import com.fixmycar.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visit Controller", description = "API для контроля посещений")
public class VisitController {

    private final VisitService visitService;

    @Autowired
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/count")
    @Operation(summary = "Получить количество всех запросов на данный URL",
            description = "Возвращает количество запросов, которые "
                    + "были сделаны на данный URL с момента запуска")
    @ApiResponse(responseCode = "200", description = "Запрос обработан успешно")
    public ResponseEntity<Integer> getVisitCount(@RequestParam String url) {
        return ResponseEntity.ok(visitService.getVisitCount(url));
    }

    @GetMapping("/total")
    @Operation(summary = "Получить общее количество всех запросов на сайт",
            description = "Возвращает общее количество всех запросов на сайт с момента запуска")
    public ResponseEntity<Integer> getTotalVisitCount() {
        return ResponseEntity.ok(visitService.getTotalVisitCount());
    }

    @GetMapping("/all")
    @Operation(summary = "Получить количество всех запросов на каждый URL сайта",
            description = "Возвращает URL, на которые были сделаны запросы "
                    + "с момента запуска, и их количество")
    public ResponseEntity<Map<String, Integer>> getAllVisitCounts() {
        return ResponseEntity.ok(visitService.getAllVisitCounts());

    }

    @DeleteMapping("/reset")
    @Operation(summary = "Сбросить все счётчики посещений")
    @ApiResponse(responseCode = "204", description = "Все счётчики сброшены")
    public ResponseEntity<Void> resetAllVisitCounts() {
        visitService.resetAllVisitCounts();
        return ResponseEntity.noContent().build();
    }
}