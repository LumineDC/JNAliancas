package br.com.senac.jnaliancas.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

/** Regras reutilizadas no projeto web e derivadas do conjunto de testes da Etapa 7. */
public final class RegrasNegocio {
    private RegrasNegocio() {}
    public static float calcularValorPendente(float total, float pago) { return Math.max(0f, Math.round((total-pago)*100f)/100f); }
    public static float calcularValorTotalPedido(float preco, int quantidade) { return Math.round(preco*quantidade*100f)/100f; }
    public static boolean temEstoqueSuficiente(int estoque, int quantidade) { return quantidade > 0 && estoque >= quantidade; }
    public static int calcularEstoqueRestante(int estoque, int quantidade) { if (!temEstoqueSuficiente(estoque, quantidade)) throw new IllegalArgumentException("Estoque insuficiente"); return estoque-quantidade; }
    public static boolean validarValorMonetario(String s) { return s != null && Pattern.matches("\\d+\\.\\d{1,2}",s.trim()); }
    public static boolean validarQuantidade(String s) { return s != null && Pattern.matches("\\d+",s.trim()); }
    public static boolean validarData(String s) { try { LocalDate.parse(s.trim(), DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)); return true; } catch(Exception e) { return false; } }
    public static boolean validarHora(String s) { try { LocalTime.parse(s.trim(), DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT)); return true; } catch(Exception e) { return false; } }
}
