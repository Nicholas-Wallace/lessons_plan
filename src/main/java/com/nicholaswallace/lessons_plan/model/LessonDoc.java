package com.nicholaswallace.lessons_plan.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class LessonDoc {

    @Getter @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Getter @Setter
    @Column(nullable = true)
    private String objetivo;

    @Getter @Setter
    @Column(nullable = true)
    private String conteudo;

    @Getter @Setter
    @Column(nullable = true)
    private String metodologia;

    @Getter @Setter
    @Column(nullable = true)
    private String recursos;

    @Getter @Setter
    @Column(nullable = true)
    private String atividadeAvaliativa;

    @Getter @Setter
    @Column(nullable = true)
    private String bibliografia;

    @Getter @Setter
    @Column(nullable = true)
    private String publicoAlvo;

    @Getter @Setter
    @Column(nullable = true)
    private String disciplina;

    @Getter @Setter
    @Column(nullable = true)
    private String tempodeAula;

    @Getter @Setter
    @Column(nullable = false)
    private Date data;
}