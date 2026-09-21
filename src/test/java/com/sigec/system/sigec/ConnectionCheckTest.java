package com.sigec.system.sigec;

import com.sigec.system.sigec.DAOS.UserDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionCheckTest {

    @Test
    public void testAuthenticationVariants() throws Exception {
        // Roberto Alves
        assertTrue(UserDAO.autenticar("roberto.admin@sigec.com", "admin"));
        assertTrue(UserDAO.autenticar("roberto.admin@sigec.com", "hash_admin"));
        assertTrue(UserDAO.autenticar("ROBERTO.ADMIN@SIGEC.COM", "admin"));

        // Carlos Alberto
        assertTrue(UserDAO.autenticar("carlos.chef@sigec.com", "prof"));
        assertTrue(UserDAO.autenticar("carlos.chef@sigec.com", "hash_prof"));

        // Mariana Souza (senha gravada no banco: 'hash_gestor')
        assertTrue(UserDAO.autenticar("mariana.gestora@sigec.com", "gestor") || UserDAO.autenticar("mariana.gestora@sigec.com", "gestora") || UserDAO.autenticar("mariana.gestora@sigec.com", "hash_gestor"));

        System.out.println(">>> TODOS OS USUARIOS AUTENTICADOS COM SUCESSO!");
    }
}
