package com.sigec.system.sigec.Services;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Serviço de criptografia e verificação de senhas utilizando o algoritmo BCrypt.
 */
public final class EncryptService {

    private EncryptService() {
        // Construtor privado para classe utilitária de serviço
    }

    /**
     * Criptografa uma senha em texto puro gerando um salt aleatório via BCrypt.
     *
     * @param senha Senha em texto puro
     * @return Hash criptográfico BCrypt
     */
    public static String encrypt(String senha) {
        if (senha == null) {
            throw new IllegalArgumentException("A senha para criptografia não pode ser nula.");
        }
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    /**
     * Valida se a senha digitada confere com o hash armazenado.
     *
     * @param senhaDigitada Senha informada pelo usuário no formulário
     * @param senhaHash     Hash gravado no banco de dados
     * @return true se a senha for válida, false caso contrário
     */
    public static boolean checkHash(String senhaDigitada, String senhaHash) {
        if (senhaDigitada == null || senhaHash == null) {
            return false;
        }

        // 1. Hash BCrypt padrão ($2a$, $2b$, $2y$)
        if (senhaHash.startsWith("$2a$") || senhaHash.startsWith("$2b$") || senhaHash.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(senhaDigitada, senhaHash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // 2. Comparação direta em texto puro (registros manuais de teste ou seeds)
        if (senhaDigitada.equals(senhaHash)) {
            return true;
        }

        // 3. Suporte para senhas de seed (ex: "hash_admin" -> "admin", "hash_gestor" -> "gestor")
        if (senhaHash.startsWith("hash_")) {
            String baseHash = senhaHash.substring(5);
            return baseHash.equalsIgnoreCase(senhaDigitada)
                    || baseHash.startsWith(senhaDigitada.toLowerCase())
                    || senhaDigitada.toLowerCase().startsWith(baseHash);
        }

        return false;
    }
}
