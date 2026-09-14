package com.sigec.system.sigec.DAOS;

import javafx.scene.control.Alert;

public class User {
    private int id_usuario;
    private String nome;
    private String email;
    private String senha;
    private String acesso;
    private String situação;
    private int token;

    public void User(int id_usuario, String nome, String email, String senha, String acesso, String situação, int token) {

        if(nome.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR CODE 404");
            alert.setHeaderText(null);
            alert.setContentText("usuário não encontrado!");
            alert.showAndWait();
            return;
        }
        if(email.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR CODE 404");
            alert.setHeaderText(null);
            alert.setContentText("email não encontrado não encontrado!");
            alert.showAndWait();
            return;
        }

        this.id_usuario = id_usuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.acesso = acesso;
        this.situação = situação;
        this.token = token;
    }


}
