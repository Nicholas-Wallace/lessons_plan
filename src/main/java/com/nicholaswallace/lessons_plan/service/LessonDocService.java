package com.nicholaswallace.lessons_plan.service;

import com.nicholaswallace.lessons_plan.dto.LessonDocDTO;
import com.nicholaswallace.lessons_plan.model.AppUser;
import com.nicholaswallace.lessons_plan.model.LessonDoc;
import com.nicholaswallace.lessons_plan.repository.LessonDocRepository;
import com.nicholaswallace.lessons_plan.repository.AppUserRepository;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LessonDocService {

    private final LessonDocRepository lessonDocRepository;
    private final AppUserRepository userRepository;

    public LessonDocService(LessonDocRepository lessonDocRepository, AppUserRepository userRepository) {
        this.lessonDocRepository = lessonDocRepository;
        this.userRepository = userRepository;
    }

    public LessonDoc create(LessonDocDTO dto) {
        AppUser user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        LessonDoc lessonDoc = new LessonDoc();
        lessonDoc.setUser(user);
        lessonDoc.setObjetivo(dto.getObjetivo());
        lessonDoc.setConteudo(dto.getConteudo());
        lessonDoc.setMetodologia(dto.getMetodologia());
        lessonDoc.setRecursos(dto.getRecursos());
        lessonDoc.setAtividadeAvaliativa(dto.getAtividadeAvaliativa());
        lessonDoc.setBibliografia(dto.getBibliografia());
        lessonDoc.setPublicoAlvo(dto.getPublicoAlvo());
        lessonDoc.setDisciplina(dto.getDisciplina());
        lessonDoc.setTempodeAula(dto.getTempodeAula());
        lessonDoc.setData(dto.getData() != null ? dto.getData() : new Date());

        return lessonDocRepository.save(lessonDoc);
    }
}