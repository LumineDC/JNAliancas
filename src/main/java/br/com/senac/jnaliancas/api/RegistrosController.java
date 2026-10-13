package br.com.senac.jnaliancas.api;

import br.com.senac.jnaliancas.service.RegistrosService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registros")
public class RegistrosController {
    private final RegistrosService service;
    public RegistrosController(RegistrosService service) { this.service=service; }
    @GetMapping("/{entidade}")
    public List<Map<String,Object>> listar(@PathVariable String entidade) { return service.listar(entidade); }
    @PostMapping("/{entidade}")
    public ResponseEntity<Map<String,Object>> criar(@PathVariable String entidade,@RequestBody Map<String,Object> dados) { return ResponseEntity.status(HttpStatus.CREATED).body(service.salvarItem(entidade,null,dados)); }
    @PutMapping("/{entidade}/{id}")
    public Map<String,Object> atualizar(@PathVariable String entidade,@PathVariable Long id,@RequestBody Map<String,Object> dados) { return service.salvarItem(entidade,id,dados); }
    @DeleteMapping("/{entidade}/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String entidade,@PathVariable Long id) { service.removerItem(entidade,id); return ResponseEntity.noContent().build(); }
}
