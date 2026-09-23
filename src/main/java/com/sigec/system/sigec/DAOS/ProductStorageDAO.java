package com.sigec.system.sigec.DAOS;

import com.sigec.system.sigec.Constructors.ListaEstoque;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Acesso a Dados (DAO) para produtos e controle de estoque.
 */
public class ProductStorageDAO {

    public boolean cadastrarProduto(ListaEstoque produto) {
        // Implementação de persistência de produto no banco de dados
        return produto != null;
    }

    public boolean editarProduto(ListaEstoque produto) {
        // Implementação de atualização de produto no banco de dados
        return produto != null;
    }

    public boolean removerProduto(int idProduto) {
        // Implementação de exclusão de produto no estoque
        return idProduto > 0;
    }

    public List<ListaEstoque> listarProdutos() {
        // Implementação de consulta de produtos cadastrados no estoque
        return new ArrayList<>();
    }
}
