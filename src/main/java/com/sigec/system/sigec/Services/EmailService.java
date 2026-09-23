package com.sigec.system.sigec.Services;

import java.util.regex.Pattern;

/**
 * Serviço de regras de validação e comunicação de e-mail do sistema SIGEC.
 */
public final class EmailService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private EmailService() {
        // Construtor privado para utilitário de serviço
    }

    /**
     * Valida sintaticamente se a string é um endereço de e-mail válido.
     *
     * @param email E-mail a ser verificado
     * @return true se o formato estiver correto, false caso contrário
     */
    public static boolean isEmailValido(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Alias para isEmailValido para conveniência semântica.
     *
     * @param email E-mail a ser verificado
     * @return true se o formato estiver correto, false caso contrário
     */
    public static boolean validarFormatoEmail(String email) {
        return isEmailValido(email);
    }

    /**
     * Envia o token de recuperação de senha por e-mail para o usuário.
     *
     * @param destinatario E-mail do usuário
     * @param token        Token gerado de 6 dígitos
     * @return true se enviado com sucesso
     */
    public static boolean enviarTokenRecuperacao(String destinatario, int token) {
        if (!isEmailValido(destinatario)) {
            return false;
        }
        // Simulação de envio seguro de e-mail / integração SMTP
        System.out.println("E-mail com token [" + token + "] enviado para: " + destinatario);
        return true;
    }
}
