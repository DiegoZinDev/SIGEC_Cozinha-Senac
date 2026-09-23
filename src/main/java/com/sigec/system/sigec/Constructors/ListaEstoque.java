package com.sigec.system.sigec.Constructors;

import java.util.Objects;

/**
 * Modelo de domínio para itens do estoque na visualização de listagem.
 */
public class ListaEstoque {

    private int idProduto;
    private String nomeProduto;
    private String tipoProduto;
    private double quantidadeProduto;
    private String unidadeProduto;

    public ListaEstoque() {
    }

    public ListaEstoque(String nomeProduto, String tipoProduto, double quantidadeProduto, String unidadeProduto) {
        this.nomeProduto = nomeProduto;
        this.tipoProduto = tipoProduto;
        this.quantidadeProduto = quantidadeProduto;
        this.unidadeProduto = unidadeProduto;
    }

    public ListaEstoque(int idProduto, String nomeProduto, String tipoProduto, double quantidadeProduto, String unidadeProduto) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.tipoProduto = tipoProduto;
        this.quantidadeProduto = quantidadeProduto;
        this.unidadeProduto = unidadeProduto;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public double getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(double quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }

    public String getUnidadeProduto() {
        return unidadeProduto;
    }

    public void setUnidadeProduto(String unidadeProduto) {
        this.unidadeProduto = unidadeProduto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaEstoque that = (ListaEstoque) o;
        return idProduto == that.idProduto && Objects.equals(nomeProduto, that.nomeProduto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduto, nomeProduto);
    }

    @Override
    public String toString() {
        return "ListaEstoque{" +
                "idProduto=" + idProduto +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", tipoProduto='" + tipoProduto + '\'' +
                ", quantidadeProduto=" + quantidadeProduto +
                ", unidadeProduto='" + unidadeProduto + '\'' +
                '}';
    }
}
