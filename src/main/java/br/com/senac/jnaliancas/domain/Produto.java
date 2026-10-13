package br.com.senac.jnaliancas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity @Table(name="produto")
public class Produto extends RegistroBase {
    @Column(nullable=false, length=120) public String nome;
    @Column(nullable=false, length=80) public String material;
    @Column(nullable=false) public Integer quantidade;
    @Column(nullable=false, precision=12, scale=2) public BigDecimal preco;
    @Column(nullable=false, length=80) public String categoria;
    @Column(length=500) public String observacoes;
    public Produto() {}
}
