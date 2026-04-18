/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.model.Agendamento;
import br.com.senac.jnaliancas.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author user
 */
public class AgendamentoDAO {
    public static void cadastrar(Agendamento a) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.persist(a);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            JPAUtil.desconectar();
        }
    }

    public List<Agendamento> listar() {
        EntityManager em = JPAUtil.conectar();
        try {
            Query consulta = em.createQuery("SELECT a FROM Agendamento a");
            List<Agendamento> receitas = consulta.getResultList();
            return receitas;
        } finally {
            JPAUtil.desconectar();
        }
    }

    public Agendamento obter(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            return em.find(Agendamento.class, id);
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void atualizar(Agendamento a) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.merge(a);
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
            Agendamento a = em.find(Agendamento.class, id);
            if (a != null) {
                em.getTransaction().begin();
                em.remove(a);
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
