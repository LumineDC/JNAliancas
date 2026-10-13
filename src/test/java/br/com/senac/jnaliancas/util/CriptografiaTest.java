package br.com.senac.jnaliancas.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários da criptografia usada no login (senha armazenada como hash MD5).
 */
class CriptografiaTest {

    @Test
    @DisplayName("MD5 de valores conhecidos")
    void md5ValoresConhecidos() {
        assertEquals("e10adc3949ba59abbe56e057f20f883e", Criptografia.getMD5("123456"));
        assertEquals("21232f297a57a5a743894a0e4a801fc3", Criptografia.getMD5("admin"));
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", Criptografia.getMD5(""));
    }

    @Test
    @DisplayName("Hash sempre tem 32 caracteres hexadecimais")
    void hashTem32Caracteres() {
        assertTrue(Criptografia.getMD5("senha").matches("^[0-9a-f]{32}$"));
    }

    @Test
    @DisplayName("Mesma entrada gera o mesmo hash; entradas diferentes geram hashes diferentes")
    void determinismo() {
        assertEquals(Criptografia.getMD5("abc"), Criptografia.getMD5("abc"));
        assertNotEquals(Criptografia.getMD5("abc"), Criptografia.getMD5("abd"));
    }
}
