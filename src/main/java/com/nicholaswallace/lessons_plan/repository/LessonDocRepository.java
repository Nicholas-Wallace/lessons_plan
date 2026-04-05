package com.nicholaswallace.lessons_plan.repository;

import com.nicholaswallace.lessons_plan.model.LessonDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonDocRepository extends JpaRepository<LessonDoc, Long> {
}
