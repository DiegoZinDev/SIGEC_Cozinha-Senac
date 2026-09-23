package com.sigec.system.sigec.Constructors;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de domínio para registros do histórico de movimentações do sistema SIGEC.
 */
public class Historico {

    private int idHistorico;
    private LocalDateTime dataHora;
    private String nomeProduto;
    private String tipoEstoque;
    private double quantidade;
    private String tipoMovimentacao;
    private String nomeUsuario;

    public Historico() {
    }

    public Historico(LocalDateTime dataHora, String nomeProduto, String tipoEstoque, double quantidade, String tipoMovimentacao, String nomeUsuario) {
        this.dataHora = dataHora;
        this.nomeProduto = nomeProduto;
        this.tipoEstoque = tipoEstoque;
        this.quantidade = quantidade;
        this.tipoMovimentacao = tipoMovimentacao;
        this.nomeUsuario = nomeUsuario;
    }

    public Historico(int idHistorico, LocalDateTime dataHora, String nomeProduto, String tipoEstoque, double quantidade, String tipoMovimentacao, String nomeUsuario) {
        this.idHistorico = idHistorico;
        this.dataHora = dataHora;
        this.nomeProduto = nomeProduto;
        this.tipoEstoque = tipoEstoque;
        this.quantidade = quantidade;
        this.tipoMovimentacao = tipoMovimentacao;
        this.nomeUsuario = nomeUsuario;
    }

    public int getIdHistorico() {
        return idHistorico;
    }

    public void setIdHistorico(int idHistorico) {
        this.idHistorico = idHistorico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(String tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Historico historico = (Historico) o;
        return idHistorico == historico.idHistorico && Objects.equals(dataHora, historico.dataHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idHistorico, dataHora);
    }

    @Override
    public String toString() {
        return "Historico{" +
                "idHistorico=" + idHistorico +
                ", dataHora=" + dataHora +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", tipoEstoque='" + tipoEstoque + '\'' +
                ", quantidade=" + quantidade +
                ", tipoMovimentacao='" + tipoMovimentacao + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                '}';
    }
}
