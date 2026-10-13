package br.com.senac.jnaliancas.repository;
import br.com.senac.jnaliancas.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PagamentoRepository extends JpaRepository<Pagamento,Long> { boolean existsByCliente_Id(Long id); }
