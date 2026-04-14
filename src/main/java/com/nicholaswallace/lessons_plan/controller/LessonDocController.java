package com.nicholaswallace.lessons_plan.controller;

import com.nicholaswallace.lessons_plan.dto.LessonDocDTO;
import com.nicholaswallace.lessons_plan.model.LessonDoc;
import com.nicholaswallace.lessons_plan.service.LessonDocService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lesson-docs")
public class LessonDocController {

    private final LessonDocService lessonDocService;

    public LessonDocController(LessonDocService lessonDocService) {
        this.lessonDocService = lessonDocService;
    }

    @PostMapping
    public ResponseEntity<LessonDoc> create(@RequestBody LessonDocDTO dto) {
        LessonDoc created = lessonDocService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}