/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.util.JPAUtil;
import br.com.senac.jnaliancas.model.Pagamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author user
 */
public class PagamentoDAO {
    
    public static void cadastrar(Pagamento p) {
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

    public List<Pagamento> listar() {
        EntityManager em = JPAUtil.conectar();
        try {
            Query consulta = em.createQuery("SELECT p FROM Pagamento p");
            List<Pagamento> lp = consulta.getResultList();
            return lp;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public Pagamento obter(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            return em.find(Pagamento.class, id);
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void atualizar(Pagamento p) {
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
            Pagamento p = em.find(Pagamento.class, id);
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
