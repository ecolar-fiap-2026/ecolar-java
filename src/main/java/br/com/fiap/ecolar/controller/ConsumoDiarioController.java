package br.com.fiap.ecolar.controller;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.service.ConsumoDiarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/consumos")
public class ConsumoDiarioController {
    private final ConsumoDiarioService service;
    public ConsumoDiarioController(ConsumoDiarioService service) { this.service=service; }
    @GetMapping public List<ConsumoDiarioSaida> listar() { return service.listar(); }
    @GetMapping("/{id}") public ConsumoDiarioSaida buscar(@PathVariable @Positive long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<ConsumoDiarioSaida> cadastrar(@RequestBody @Valid ConsumoDiarioEntrada entrada) {
        var saida=service.cadastrar(entrada);
        URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saida.getCdConsumo()).toUri();
        return ResponseEntity.created(location).body(saida);
    }
    @PutMapping("/{id}") public ConsumoDiarioSaida atualizar(@PathVariable @Positive long id,@RequestBody @Valid ConsumoDiarioEntrada entrada) { return service.atualizar(id,entrada); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable @Positive long id) { service.remover(id); return ResponseEntity.noContent().build(); }
}
