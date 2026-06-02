package com.nicholaswallace.demo.lessondoc;

import com.nicholaswallace.demo.lessondoc.LessonDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonDocRepository extends JpaRepository<LessonDoc, Long> {
}
