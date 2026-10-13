package br.com.senac.jnaliancas.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/** Compatibilidade histórica com a Etapa 7; não usar MD5 para senhas em produção. */
public final class Criptografia {
    private Criptografia() {}
    public static String getMD5(String valor) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(valor.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
}
