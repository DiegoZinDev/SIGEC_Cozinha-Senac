package com.sigec.system.sigec.DAOS;

import com.sigec.system.sigec.Constructors.Historico;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Acesso a Dados (DAO) para o histórico e auditoria de movimentações.
 */
public class HistoryDAO {

    public List<Historico> listarHistorico() {
        // Implementação de busca de todas as movimentações registradas
        return new ArrayList<>();
    }

    public List<Historico> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        // Implementação de filtragem por intervalo de datas
        return new ArrayList<>();
    }

    public boolean registrarMovimentacao(Historico historico) {
        return historico != null;
    }
}
