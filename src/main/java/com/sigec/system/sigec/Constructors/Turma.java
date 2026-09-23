package com.sigec.system.sigec.Constructors;

import java.util.Objects;

/**
 * Modelo de domínio representando uma Turma no sistema SIGEC.
 */
public class Turma {

    private int idTurma;
    private String nomeTurma;
    private String laboratorio;
    private String professorResponsavel;
    private int idProfessor;
    private String situacao;

    public Turma() {
        this.situacao = "Ativo";
    }

    public Turma(String nomeTurma, String laboratorio, String professorResponsavel, String situacao) {
        this.nomeTurma = nomeTurma;
        this.laboratorio = laboratorio;
        this.professorResponsavel = professorResponsavel;
        this.situacao = (situacao != null && !situacao.isBlank()) ? situacao : "Ativo";
    }

    public Turma(int idTurma, String nomeTurma, String laboratorio, String professorResponsavel, int idProfessor, String situacao) {
        this.idTurma = idTurma;
        this.nomeTurma = nomeTurma;
        this.laboratorio = laboratorio;
        this.professorResponsavel = professorResponsavel;
        this.idProfessor = idProfessor;
        this.situacao = (situacao != null && !situacao.isBlank()) ? situacao : "Ativo";
    }

    public int getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(int idTurma) {
        this.idTurma = idTurma;
    }

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getProfessorResponsavel() {
        return professorResponsavel;
    }

    public void setProfessorResponsavel(String professorResponsavel) {
        this.professorResponsavel = professorResponsavel;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turma turma = (Turma) o;
        return idTurma == turma.idTurma && Objects.equals(nomeTurma, turma.nomeTurma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTurma, nomeTurma);
    }

    @Override
    public String toString() {
        return "Turma{" +
                "idTurma=" + idTurma +
                ", nomeTurma='" + nomeTurma + '\'' +
                ", laboratorio='" + laboratorio + '\'' +
                ", professorResponsavel='" + professorResponsavel + '\'' +
                ", situacao='" + situacao + '\'' +
                '}';
    }
}
