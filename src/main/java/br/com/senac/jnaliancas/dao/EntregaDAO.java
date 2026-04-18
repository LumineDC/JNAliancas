/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.model.Entrega;
import br.com.senac.jnaliancas.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author user
 */
public class EntregaDAO {
    
    public static void cadastrar(Entrega e) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.persist(e);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        } finally {
            JPAUtil.desconectar();
        }
    }

    public List<Entrega> listar() {
        EntityManager em = JPAUtil.conectar();
        try {
            Query consulta = em.createQuery("SELECT e FROM Entrega e");
            List<Entrega> le = consulta.getResultList();
            return le;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public Entrega obter(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            return em.find(Entrega.class, id);
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void atualizar(Entrega e) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.merge(e);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void excluir(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            Entrega e = em.find(Entrega.class, id);
            if (e != null) {
                em.getTransaction().begin();
                em.remove(e);
                em.getTransaction().commit();
            }
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        } finally {
            JPAUtil.desconectar();
        }
    }
}
