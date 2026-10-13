package br.com.senac.jnaliancas.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name="cliente")
public class Cliente extends RegistroBase {
    @Column(nullable=false, length=120) public String nome;
    @Column(nullable=false, length=10) public String nascimento;
    @Column(nullable=false, length=14) public String cpf;
    @Column(nullable=false, length=25) public String telefone;
    @Column(nullable=false, length=120) public String rua;
    @Column(nullable=false, length=12) public String numero;
    @Column(nullable=false, length=100) public String bairro;
    @Column(nullable=false, length=100) public String cidade;
    @Column(nullable=false, length=2) public String estado;
    @Column(nullable=false, length=9) public String cep;
    @Column(length=120) public String complemento;
    public Cliente() {}
}
