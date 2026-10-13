package br.com.senac.jnaliancas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="pedido")
public class Pedido extends RegistroBase {
    // These associations are read through public fields by the service and API mapper.
    // Eager loading prevents Hibernate proxies from exposing null field values.
    @ManyToOne(fetch=FetchType.EAGER, optional=false) @JoinColumn(name="cliente_id", nullable=false) public Cliente cliente;
    @ManyToOne(fetch=FetchType.EAGER, optional=false) @JoinColumn(name="produto_id", nullable=false) public Produto produto;
    @Column(nullable=false) public Integer quantidade;
    @Column(nullable=false, length=10) public String data;
    @Column(length=500) public String observacoes;
    @Column(nullable=false, length=3) public String concluido;
    @Column(nullable=false, precision=12, scale=2) public BigDecimal total;
    public Pedido() {}
}
