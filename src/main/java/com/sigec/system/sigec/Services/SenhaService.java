package com.sigec.system.sigec.Services;

import java.security.SecureRandom;

/**
 * Serviço de regras de negócio e validações para senhas e tokens de recuperação.
 */
public final class SenhaService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaService() {
        // Construtor privado para utilitário de serviço
    }

    /**
     * Valida se a senha atende aos requisitos mínimos de segurança do sistema.
     * Requisitos: mínimo 6 caracteres, não nula e não vazia.
     *
     * @param senha Senha informada
     * @return true se a senha for válida, false caso contrário
     */
    public static boolean isSenhaValida(String senha) {
        if (senha == null || senha.isBlank()) {
            return false;
        }
        return senha.trim().length() >= 6;
    }

    /**
     * Gera um token numérico aleatório de 6 dígitos para recuperação de senha.
     *
     * @return Código numérico de 6 dígitos formatado
     */
    public static int gerarTokenRecuperacao() {
        return 100000 + RANDOM.nextInt(900000);
    }
}
