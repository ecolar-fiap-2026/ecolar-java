package br.com.fiap.ecolar.controller;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.service.MetaConsumoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/metas")
public class MetaConsumoController {
    private final MetaConsumoService service;
    public MetaConsumoController(MetaConsumoService service) { this.service=service; }
    @GetMapping public List<MetaConsumoSaida> listar() { return service.listar(); }
    @GetMapping("/{id}") public MetaConsumoSaida buscar(@PathVariable @Positive long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<MetaConsumoSaida> cadastrar(@RequestBody @Valid MetaConsumoEntrada entrada) {
        var saida=service.cadastrar(entrada);
        URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saida.getCdMeta()).toUri();
        return ResponseEntity.created(location).body(saida);
    }
    @PutMapping("/{id}") public MetaConsumoSaida atualizar(@PathVariable @Positive long id,@RequestBody @Valid MetaConsumoEntrada entrada) { return service.atualizar(id,entrada); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable @Positive long id) { service.remover(id); return ResponseEntity.noContent().build(); }
}
