package br.com.senac.jnaliancas.dao;

import br.com.senac.jnaliancas.util.JPAUtil;
import br.com.senac.jnaliancas.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;


public class UsuarioBD {

    public static Usuario validarUsuario(Usuario usuario) {
        
        Usuario usuarioEncontrado = null;

        try {
            EntityManager em = JPAUtil.conectar();

            String jpql = "SELECT u FROM Usuario u WHERE u.login = :login AND u.senha = :senha";

            TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
            query.setParameter("login", usuario.getLogin());
            query.setParameter("senha", usuario.getSenha());

            usuarioEncontrado = query.getSingleResult();

            em.close();

        } catch (NoResultException e) {
            return null; // usuário não encontrado
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarioEncontrado;
    }
    
}
   