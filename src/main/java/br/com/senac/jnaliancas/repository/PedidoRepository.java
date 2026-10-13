package br.com.senac.jnaliancas.repository;
import br.com.senac.jnaliancas.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PedidoRepository extends JpaRepository<Pedido,Long> { boolean existsByProduto_Id(Long id); boolean existsByCliente_Id(Long id); }
