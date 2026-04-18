
package br.com.senac.jnaliancas.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static String PERSISTENSE_UNIT = "Aliancas-PU";
    private static EntityManager em;
    private static EntityManagerFactory fabrica;
    
    public static EntityManager conectar() {
        if (fabrica == null || !fabrica.isOpen()) {
            fabrica = Persistence.createEntityManagerFactory(PERSISTENSE_UNIT);
        }

        if (em == null || !em.isOpen()) {
            em = fabrica.createEntityManager();
        }

        return em;
    }

    public static void desconectar() {
        if (em.isOpen() && em != null) {
            em.close();
            fabrica.close();
        }
    }
}
