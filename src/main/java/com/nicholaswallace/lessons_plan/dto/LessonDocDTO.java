package com.nicholaswallace.lessons_plan.dto;

import java.util.Date;

public class LessonDocDTO {

    private Long userId;
    private String objetivo;
    private String conteudo;
    private String metodologia;
    private String recursos;
    private String atividadeAvaliativa;
    private String bibliografia;
    private String publicoAlvo;
    private String disciplina;
    private String tempodeAula;
    private Date data;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }

    public String getRecursos() { return recursos; }
    public void setRecursos(String recursos) { this.recursos = recursos; }

    public String getAtividadeAvaliativa() { return atividadeAvaliativa; }
    public void setAtividadeAvaliativa(String atividadeAvaliativa) { this.atividadeAvaliativa = atividadeAvaliativa; }

    public String getBibliografia() { return bibliografia; }
    public void setBibliografia(String bibliografia) { this.bibliografia = bibliografia; }

    public String getPublicoAlvo() { return publicoAlvo; }
    public void setPublicoAlvo(String publicoAlvo) { this.publicoAlvo = publicoAlvo; }

    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }

    public String getTempodeAula() { return tempodeAula; }
    public void setTempodeAula(String tempodeAula) { this.tempodeAula = tempodeAula; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }
}