
package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.util.JPAUtil;
import br.com.senac.jnaliancas.model.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

public class PedidoDAO {
    public static void cadastrar(Pedido p) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            JPAUtil.desconectar();
        }
    }

    public List<Pedido> listar() {
        EntityManager em = JPAUtil.conectar();
        try {
            Query consulta = em.createQuery("SELECT p FROM Pedido p");
            List<Pedido> lp = consulta.getResultList();
            return lp;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public Pedido obter(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            return em.find(Pedido.class, id);
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void atualizar(Pedido p) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void excluir(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            Pedido p = em.find(Pedido.class, id);
            if (p != null) {
                em.getTransaction().begin();
                em.remove(p);
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            JPAUtil.desconectar();
        }
    }
}
