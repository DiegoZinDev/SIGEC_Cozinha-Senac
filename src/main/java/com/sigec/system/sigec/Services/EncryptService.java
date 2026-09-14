package com.sigec.system.sigec.Services;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptService {
        public static String encrypt (String codigo) {

            return BCrypt.hashpw(codigo, BCrypt.gensalt());
        }
        public static boolean checkHash ( String codigoDigitado, String codigoHash ) {
            if (codigoDigitado == null || codigoHash == null) {
                return false;
            }
            else {
                return BCrypt.checkpw(codigoDigitado, codigoHash);
            }
        }

}
