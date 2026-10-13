package br.com.senac.jnaliancas.repository;
import br.com.senac.jnaliancas.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProdutoRepository extends JpaRepository<Produto,Long> { Produto findFirstByNomeIgnoreCase(String nome); }
