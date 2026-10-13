package br.com.senac.jnaliancas.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes unitários das regras de negócio da JN Alianças (sem banco de dados e sem interface gráfica).
 */
class RegrasNegocioTest {

    // ---------- Pagamento: valor pendente ----------

    @Test
    @DisplayName("Pagamento parcial: pendente = total - pago")
    void valorPendentePagamentoParcial() {
        assertEquals(50.00f, RegrasNegocio.calcularValorPendente(150.00f, 100.00f), 0.001f);
    }

    @Test
    @DisplayName("Pagamento integral: pendente zerado")
    void valorPendentePagamentoIntegral() {
        assertEquals(0f, RegrasNegocio.calcularValorPendente(299.90f, 299.90f), 0.001f);
    }

    @Test
    @DisplayName("Pago maior que o total: pendente nunca fica negativo")
    void valorPendenteNaoFicaNegativo() {
        assertEquals(0f, RegrasNegocio.calcularValorPendente(100.00f, 120.00f), 0.001f);
    }

    @Test
    @DisplayName("Centavos: 100.10 - 50.05 = 50.05 (sem erro de ponto flutuante)")
    void valorPendenteComCentavos() {
        assertEquals(50.05f, RegrasNegocio.calcularValorPendente(100.10f, 50.05f), 0.001f);
    }

    // ---------- Pedido: valor total ----------

    @Test
    @DisplayName("Valor total do pedido = preço x quantidade")
    void valorTotalPedido() {
        assertEquals(1200.00f, RegrasNegocio.calcularValorTotalPedido(600.00f, 2), 0.001f);
        assertEquals(59.97f, RegrasNegocio.calcularValorTotalPedido(19.99f, 3), 0.001f);
    }

    @Test
    @DisplayName("Quantidade zero resulta em valor zero")
    void valorTotalPedidoQuantidadeZero() {
        assertEquals(0f, RegrasNegocio.calcularValorTotalPedido(600.00f, 0), 0.001f);
    }

    // ---------- Estoque ----------

    @Test
    @DisplayName("Estoque suficiente: quantidade menor ou igual ao estoque")
    void estoqueSuficiente() {
        assertTrue(RegrasNegocio.temEstoqueSuficiente(10, 4));
        assertTrue(RegrasNegocio.temEstoqueSuficiente(10, 10)); // valor limite
    }

    @Test
    @DisplayName("Estoque insuficiente: quantidade maior que o estoque ou inválida")
    void estoqueInsuficiente() {
        assertFalse(RegrasNegocio.temEstoqueSuficiente(10, 11)); // valor limite
        assertFalse(RegrasNegocio.temEstoqueSuficiente(0, 1));
        assertFalse(RegrasNegocio.temEstoqueSuficiente(10, 0));
        assertFalse(RegrasNegocio.temEstoqueSuficiente(10, -3));
    }

    @Test
    @DisplayName("Baixa de estoque após o pedido")
    void estoqueRestante() {
        assertEquals(6, RegrasNegocio.calcularEstoqueRestante(10, 4));
        assertEquals(0, RegrasNegocio.calcularEstoqueRestante(10, 10));
    }

    @Test
    @DisplayName("Baixa de estoque acima do disponível lança exceção")
    void estoqueRestanteInsuficienteLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> RegrasNegocio.calcularEstoqueRestante(3, 5));
    }

    // ---------- Validações de entrada ----------

    @ParameterizedTest
    @ValueSource(strings = {"10.5", "10.50", "0.99", "1500.00", " 25.90 "})
    @DisplayName("Valores monetários válidos")
    void valorMonetarioValido(String valor) {
        assertTrue(RegrasNegocio.validarValorMonetario(valor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "10", "10,50", "10.505", "-5.00", "R$ 10.00", ".50"})
    @DisplayName("Valores monetários inválidos")
    void valorMonetarioInvalido(String valor) {
        assertFalse(RegrasNegocio.validarValorMonetario(valor));
    }

    @Test
    @DisplayName("Valor monetário nulo é inválido")
    void valorMonetarioNulo() {
        assertFalse(RegrasNegocio.validarValorMonetario(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "25", "0", " 7 "})
    @DisplayName("Quantidades válidas")
    void quantidadeValida(String valor) {
        assertTrue(RegrasNegocio.validarQuantidade(valor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "-1", "2.5", "dois", "1 0"})
    @DisplayName("Quantidades inválidas")
    void quantidadeInvalida(String valor) {
        assertFalse(RegrasNegocio.validarQuantidade(valor));
    }

    @ParameterizedTest
    @ValueSource(strings = {"01/01/2026", "29/02/2028", "31/12/2025"})
    @DisplayName("Datas válidas (inclui ano bissexto)")
    void dataValida(String data) {
        assertTrue(RegrasNegocio.validarData(data));
    }

    @ParameterizedTest
    @ValueSource(strings = {"31/02/2026", "29/02/2027", "32/01/2026", "01/13/2026", "2026-01-01", "dd/mm/aaaa", ""})
    @DisplayName("Datas inválidas")
    void dataInvalida(String data) {
        assertFalse(RegrasNegocio.validarData(data));
    }

    @ParameterizedTest
    @ValueSource(strings = {"00:00", "08:30", "23:59"})
    @DisplayName("Horários válidos")
    void horaValida(String hora) {
        assertTrue(RegrasNegocio.validarHora(hora));
    }

    @ParameterizedTest
    @ValueSource(strings = {"24:00", "12:60", "8h30", "abc", ""})
    @DisplayName("Horários inválidos")
    void horaInvalida(String hora) {
        assertFalse(RegrasNegocio.validarHora(hora));
    }
}
