package com.nicholaswallace.lessons_plan.dto;

import java.util.List;

public class LessonDocCompileDTO {

    public Informacoes informacoes;
    public String titulo;
    public List<String> objetivos;
    public List<ConteudoItem> conteudo;
    public List<MetodologiaItem> metodologia;
    public List<String> recursos_didaticos;
    public AtividadeAvaliativa atividade_avaliativa;
    public List<BibliografiaItem> bibliografia;

    public static class Informacoes {
        public String nome_candidato;
        public String numero_inscricao;
        public String area_concurso;
        public String publico_alvo;
        public String disciplina;
        public String tempo_aula;
        public String data_aula;
    }

    public static class ConteudoItem {
        public String titulo;
        public List<String> subtopicos;
    }

    public static class MetodologiaItem {
        public String nome;
        public String descricao;
    }

    public static class AtividadeAvaliativa {
        public String enunciado;
        public String forma_aplicacao;
        public List<CriterioCorrecao> criterios_correcao;
    }

    public static class CriterioCorrecao {
        public String criterio;
        public int peso;
    }

    public static class BibliografiaItem {
        public String autor;
        public String titulo;
        public String editora;
        public String periodico;
        public String volume;
        public Integer ano;
    }
}
