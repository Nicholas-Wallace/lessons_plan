package com.example.demo.lessondoc;

import com.example.demo.lessondoc.LessonDocCompileDTO;
import com.example.demo.lessondoc.LessonDocDTO;
import com.example.demo.lessondoc.LessonDoc;
import com.example.demo.lessondoc.LessonDocService;
import com.example.demo.lessondoc.DocxCompileService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/lesson-docs")
public class LessonDocController {

    private final LessonDocService lessonDocService;
    private final DocxCompileService docxCompileService;

    public LessonDocController(LessonDocService lessonDocService, DocxCompileService docxCompileService) {
        this.lessonDocService = lessonDocService;
        this.docxCompileService = docxCompileService;
    }

    @PostMapping
    public ResponseEntity<LessonDoc> create(@RequestBody LessonDocDTO dto) {
        LessonDoc created = lessonDocService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * POST /api/lesson-docs/compile
     *
     * Accepts the full LessonDocCompileDTO payload, generates a .docx file,
     * stores it in the configured compile directory and returns the path.
     *
     * Success  201 → { "docxPath": "/compiled/<uuid>.docx" }
     * Failure  500 → { "error": "<message>" }
     */
    @PostMapping("/compile")
    public ResponseEntity<Resource> compile(@RequestBody LessonDocCompileDTO dto) {
        try {
            String docxPath = docxCompileService.compile(dto);
            // docxPath is returned like "/compiled/<id>.docx" while storageDir is "./static/compiled"
            // avoid resolving to "./static/compiled/compiled/<id>.docx" by stripping the leading "/compiled/" prefix
            String relative = docxPath;
            if (relative.startsWith("/compiled/")) {
                relative = relative.substring("/compiled/".length());
            } else {
                relative = relative.replaceFirst("^/", "");
            }
            Path filePath = Paths.get(docxCompileService.getStorageDir()).resolve(relative);
            File file = filePath.toFile();

            if (!file.exists() || !file.canRead()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            Resource resource = new FileSystemResource(file);
            String filename = file.getName();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}