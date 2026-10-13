package br.com.senac.jnaliancas.repository;
import br.com.senac.jnaliancas.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AgendamentoRepository extends JpaRepository<Agendamento,Long> { boolean existsByCliente_Id(Long id); }
