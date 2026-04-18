
package br.com.senac.jnaliancas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "pedido")
public class Pedido {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Produto produto;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    private int quantidade;
    private LocalDate data;
    private String observacoes;
    private String concluido;

    public Pedido(){
        
    }
   
    public Pedido(int id, Produto produto, Cliente cliente, int quantidade, LocalDate data, String observacoes, String concluido) {
        this.id = id;
        this.produto = produto;
        this.cliente = cliente;
        this.quantidade = quantidade;
        this.data = data;
        this.observacoes = observacoes;
        this.concluido = concluido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getConcluido() {
        return concluido;
    }

    public void setConcluido(String concluido) {
        this.concluido = concluido;
    }
    
}
