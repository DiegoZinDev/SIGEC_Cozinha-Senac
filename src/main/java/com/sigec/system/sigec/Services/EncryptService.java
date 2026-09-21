package com.sigec.system.sigec.Services;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptService {
        public static String encrypt (String codigo) {

            return BCrypt.hashpw(codigo, BCrypt.gensalt());
        }
        public static boolean checkHash(String codigoDigitado, String codigoHash) {
            if (codigoDigitado == null || codigoHash == null) {
                return false;
            }

            // 1. Se for um hash BCrypt válido ($2a$, $2b$, $2y$)
            if (codigoHash.startsWith("$2a$") || codigoHash.startsWith("$2b$") || codigoHash.startsWith("$2y$")) {
                try {
                    return BCrypt.checkpw(codigoDigitado, codigoHash);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            // 2. Comparação direta em texto puro (ex: registros manuais no banco de dados)
            if (codigoDigitado.equals(codigoHash)) {
                return true;
            }

            // 3. Suporte para senhas de seed (ex: "hash_admin" -> "admin", "hash_gestor" -> "gestora" / "gestor")
            if (codigoHash.startsWith("hash_")) {
                String baseHash = codigoHash.substring(5);
                if (baseHash.equalsIgnoreCase(codigoDigitado)) {
                    return true;
                }
                if (baseHash.startsWith(codigoDigitado.toLowerCase()) || codigoDigitado.toLowerCase().startsWith(baseHash)) {
                    return true;
                }
            }

            return false;
        }

}
