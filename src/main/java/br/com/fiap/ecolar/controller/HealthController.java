package br.com.fiap.ecolar.controller;
import br.com.fiap.ecolar.dao.ResidenciaDao;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HealthController {
    private final ResidenciaDao dao;
    public HealthController(ResidenciaDao dao) { this.dao=dao; }
    @GetMapping("/health/live") public Map<String,String> vivo() { return Map.of("status","UP","aplicacao","EcoLar Java"); }
    @GetMapping("/health") public ResponseEntity<Map<String,String>> pronto() {
        try { dao.testarConexao(); return ResponseEntity.ok(Map.of("status","UP","banco","conectado")); }
        catch (RuntimeException e) { return ResponseEntity.status(503).body(Map.of("status","DOWN","banco","indisponivel","mensagem","Confira as variáveis Oracle e a conectividade.")); }
    }
}
