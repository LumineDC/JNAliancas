
package br.com.senac.jnaliancas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Cliente cliente;
    private float valor;
    private String tipo;
    private float pago;
    private float valorPendente;
    
    public Pagamento(){
        
    }

    public Pagamento(int id, Cliente cliente, float valor, String tipo, float pago, float valorPendente) {
        this.id = id;
        this.cliente = cliente;
        this.valor = valor;
        this.tipo = tipo;
        this.pago = pago;
        this.valorPendente = valorPendente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getPago() {
        return pago;
    }

    public void setPago(float pago) {
        this.pago = pago;
    }

    public float getValorPendente() {
        return valorPendente;
    }

    public void setValorPendente(float valorPendente) {
        this.valorPendente = valorPendente;
    }
    
}
