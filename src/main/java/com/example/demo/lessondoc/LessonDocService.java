package com.example.demo.lessondoc;

import com.example.demo.lessondoc.LessonDocDTO;
import com.example.demo.appuser.AppUser;
import com.example.demo.lessondoc.LessonDoc;
import com.example.demo.lessondoc.LessonDocRepository;
import com.example.demo.appuser.AppUserRepository;

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