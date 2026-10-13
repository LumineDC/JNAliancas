package br.com.senac.jnaliancas.repository;
import br.com.senac.jnaliancas.domain.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EntregaRepository extends JpaRepository<Entrega,Long> { boolean existsByCliente_Id(Long id); }
