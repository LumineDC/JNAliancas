package br.com.senac.jnaliancas.api;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api")
public class SessaoController {
    @GetMapping("/sessao") public Map<String,String> sessao(Authentication auth){ return Map.of("usuario",auth.getName()); }
}
