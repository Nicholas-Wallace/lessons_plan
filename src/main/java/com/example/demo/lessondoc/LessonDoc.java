package com.example.demo.lessondoc;

import com.example.demo.appuser.AppUser;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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