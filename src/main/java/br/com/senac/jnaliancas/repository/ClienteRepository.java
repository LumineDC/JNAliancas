package br.com.senac.jnaliancas.repository;
import br.com.senac.jnaliancas.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteRepository extends JpaRepository<Cliente,Long> { Cliente findFirstByNomeIgnoreCase(String nome); }
