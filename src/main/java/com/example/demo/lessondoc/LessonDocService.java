package com.nicholaswallace.demo.lessondoc;

import com.nicholaswallace.model.lessondoc.LessonDocDTO;
import com.nicholaswallace.demo.lessondoc.AppUser;
import com.nicholaswallace.demo.lessondoc.LessonDoc;
import com.nicholaswallace.demo.lessondoc.LessonDocRepository;
import com.nicholaswallace.demo.lessondoc.AppUserRepository;

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