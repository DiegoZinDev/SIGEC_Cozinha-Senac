package com.sigec.system.sigec.Constructors;

import java.util.Objects;

/**
 * Modelo de domínio representando um Usuário no sistema SIGEC.
 */
public class User {

    private int idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String acesso;
    private String situacao;
    private int token;

    public User() {
    }

    public User(String nome, String email, String senha, String acesso) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.acesso = acesso;
    }

    public User(int idUsuario, String nome, String email, String senha, String acesso, String situacao, int token) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.acesso = acesso;
        this.situacao = situacao;
        this.token = token;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getAcesso() {
        return acesso;
    }

    public void setAcesso(String acesso) {
        this.acesso = acesso;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return idUsuario == user.idUsuario && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", acesso='" + acesso + '\'' +
                ", situacao='" + situacao + '\'' +
                '}';
    }
}
