package com.sigec.system.sigec.Constructors;

import java.util.Objects;

/**
 * Modelo de domínio representando uma Turma no sistema SIGEC.
 */
public class Turma {

    private int idTurma;
    private String nomeTurma;
    private int id_laboratorio;
    private String situacao;

    public Turma() {
        this.situacao = "Ativo";
    }

    public Turma(String nomeTurma, int id_laboratorio, String situacao) {
        this.nomeTurma = nomeTurma;
        this.id_laboratorio = id_laboratorio;
        this.situacao = (situacao != null && !situacao.isBlank()) ? situacao : "Ativo";
    }

    public Turma(int idTurma, String nomeTurma, int id_laboratorio, String situacao) {
        this.idTurma = idTurma;
        this.nomeTurma = nomeTurma;
        this.id_laboratorio = id_laboratorio;
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

    public int getid_Laboratorio() {
        return id_laboratorio;
    }

    public void setLaboratorio(int laboratorio) {
        this.id_laboratorio = id_laboratorio;
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
                ", laboratorio='" + id_laboratorio + '\'' +
                ", situacao='" + situacao + '\'' +
                '}';
    }
}
