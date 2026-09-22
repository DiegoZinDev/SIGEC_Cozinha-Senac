package com.sigec.system.sigec.Utils;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Utilitário para navegação fluida por teclado em formulários (Enter-to-Next e Enter-to-Submit).
 * Permite avançar entre campos de entrada ao pressionar Enter e acionar
 * o botão de confirmação/autenticação no último campo sem necessidade de mouse.
 */
public class FormNavigationUtil {

    /**
     * Encadeia uma sequência ordenada de campos com um botão de ação final.
     * Ao pressionar Enter:
     * - Se houver um próximo campo na sequência, o foco é transferido para ele.
     * - Se for o último campo da sequência, o botão informado é acionado (.fire()).
     *
     * @param submitButton Botão a ser acionado ao pressionar Enter no último campo (pode ser null)
     * @param fields Sequência de campos na ordem desejada
     */
    public static void encadearCampos(Button submitButton, Node... fields) {
        if (fields == null || fields.length == 0) {
            return;
        }

        for (int i = 0; i < fields.length; i++) {
            Node currentField = fields[i];
            if (currentField == null) {
                continue;
            }

            final int nextIndex = i + 1;
            // Se for TextField ou PasswordField (herda de TextField), o setOnAction é o evento nativo de Enter
            if (currentField instanceof TextField tf) {
                tf.setOnAction(event -> {
                    if (nextIndex < fields.length && fields[nextIndex] != null) {
                        fields[nextIndex].requestFocus();
                    } else if (submitButton != null) {
                        submitButton.fire();
                    }
                });
            } else {
                currentField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        event.consume();
                        if (nextIndex < fields.length && fields[nextIndex] != null) {
                            fields[nextIndex].requestFocus();
                        } else if (submitButton != null) {
                            submitButton.fire();
                        }
                    }
                });
            }
        }
    }

    /**
     * Configura um campo de texto individual para disparar uma ação específica ao pressionar Enter.
     */
    public static void configurarAcaoEnter(TextField field, Runnable acao) {
        if (field != null && acao != null) {
            field.setOnAction(e -> acao.run());
        }
    }
}
