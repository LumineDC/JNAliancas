
package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.model.Cliente;
import br.com.senac.jnaliancas.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

public class ClienteDAO {
    
    public static void cadastrar(Cliente c){
        EntityManager em = JPAUtil.conectar();
        try{
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            JPAUtil.desconectar();
        }
    }
    
    public List<Cliente> listar(){
      EntityManager em = JPAUtil.conectar();
      try{
          Query consulta = em.createQuery("SELECT c FROM Cliente c");
          List<Cliente> receitas = consulta.getResultList();
          return receitas;
      }finally{
          JPAUtil.desconectar();
      }
    }  
    
    public Cliente obter(int id) {
        EntityManager em = JPAUtil.conectar();
        try {
            return em.find(Cliente.class, id);
        } finally {
            JPAUtil.desconectar();
        }
    }

    public void atualizar(Cliente c) {
        EntityManager em = JPAUtil.conectar();
        try {
            em.getTransaction().begin();
            em.merge(c);
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
            Cliente c = em.find(Cliente.class, id);
            if (c != null) {
                em.getTransaction().begin();
                em.remove(c);
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
