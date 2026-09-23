package com.sigec.system.sigec.Constructors;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Modelo de domínio para alertas de produtos com validade próxima ou estoque baixo.
 */
public class AlertaProduto {

    private int idAlerta;
    private String produto;
    private LocalDate validade;
    private String status;
    private int quantidade;

    public AlertaProduto() {
    }

    public AlertaProduto(String produto, LocalDate validade, String status, int quantidade) {
        this.produto = produto;
        this.validade = validade;
        this.status = status;
        this.quantidade = quantidade;
    }

    public AlertaProduto(int idAlerta, String produto, LocalDate validade, String status, int quantidade) {
        this.idAlerta = idAlerta;
        this.produto = produto;
        this.validade = validade;
        this.status = status;
        this.quantidade = quantidade;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertaProduto that = (AlertaProduto) o;
        return idAlerta == that.idAlerta && Objects.equals(produto, that.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAlerta, produto);
    }

    @Override
    public String toString() {
        return "AlertaProduto{" +
                "idAlerta=" + idAlerta +
                ", produto='" + produto + '\'' +
                ", validade=" + validade +
                ", status='" + status + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }
}
