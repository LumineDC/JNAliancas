/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.util.JPAUtil;
import br.com.senac.jnaliancas.model.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author user
 */
public class ProdutoDAO {
    
    public static void cadastrar(Produto p) {
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

    public List<Produto> listar() {
        EntityManager em = JPAUtil.conectar();
        try {
            Query consulta = em.createQuery("SELECT p FROM Produto p");
            List<Produto> lp = consulta.getResultList();
            return lp;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public Produto obter(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            return em.find(Produto.class, id);
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void atualizar(Produto p) {
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
            Produto p = em.find(Produto.class, id);
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
