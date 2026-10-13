package br.com.senac.jnaliancas.domain;

import jakarta.persistence.*;

@Entity @Table(name="agendamento")
public class Agendamento extends RegistroBase {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="cliente_id", nullable=false) public Cliente cliente;
    @Column(nullable=false, length=10) public String data;
    @Column(nullable=false, length=5) public String hora;
    @Column(length=500) public String observacoes;
    public Agendamento() {}
}
