package com.nicholaswallace.lessons_plan.controller;

import com.nicholaswallace.lessons_plan.dto.LessonDocCompileDTO;
import com.nicholaswallace.lessons_plan.dto.LessonDocDTO;
import com.nicholaswallace.lessons_plan.model.LessonDoc;
import com.nicholaswallace.lessons_plan.service.LessonDocService;
import com.nicholaswallace.lessons_plan.service.TexCompileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lesson-docs")
public class LessonDocController {

    private final LessonDocService lessonDocService;
    private final TexCompileService texCompileService;

    public LessonDocController(LessonDocService lessonDocService, TexCompileService texCompileService) {
        this.lessonDocService = lessonDocService;
        this.texCompileService = texCompileService;
    }

    @PostMapping
    public ResponseEntity<LessonDoc> create(@RequestBody LessonDocDTO dto) {
        LessonDoc created = lessonDocService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * POST /api/lesson-docs/compile
     *
     * Accepts the full LessonDocCompileDTO payload, fills the .tex template,
     * compiles it with pdflatex and returns the path to the generated PDF.
     *
     * Success  201 → { "pdfPath": "/compiled/<uuid>.pdf" }
     * Failure  500 → { "error": "<message>" }
     */
    @PostMapping("/compile")
    public ResponseEntity<Map<String, String>> compile(@RequestBody LessonDocCompileDTO dto) {
        try {
            String pdfPath = texCompileService.compile(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("pdfPath", pdfPath));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }
}