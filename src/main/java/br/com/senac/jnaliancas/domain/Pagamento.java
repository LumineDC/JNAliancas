package br.com.senac.jnaliancas.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="pagamento")
public class Pagamento extends RegistroBase {
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="cliente_id", nullable=false) public Cliente cliente;
    @Column(nullable=false, precision=12, scale=2) public BigDecimal valor;
    @Column(nullable=false, length=20) public String tipo;
    @Column(nullable=false, precision=12, scale=2) public BigDecimal pago;
    @Column(nullable=false, precision=12, scale=2) public BigDecimal pendente;
    public Pagamento() {}
}
