package br.com.senac.jnaliancas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="entrega")
public class Entrega extends RegistroBase {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="cliente_id", nullable=false) public Cliente cliente;
    @Column(nullable=false, length=12) public String tipo;
    @Column(precision=12, scale=2) public BigDecimal frete;
    @Column(nullable=false, length=40) public String prazo;
    @Column(length=500) public String observacoes;
    public Entrega() {}
}
