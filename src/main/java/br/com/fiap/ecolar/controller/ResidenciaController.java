package br.com.fiap.ecolar.controller;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.service.ResidenciaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/residencias")
public class ResidenciaController {
    private final ResidenciaService service;
    public ResidenciaController(ResidenciaService service) { this.service=service; }
    @GetMapping public List<ResidenciaSaida> listar() { return service.listar(); }
    @GetMapping("/{id}") public ResidenciaSaida buscar(@PathVariable @Positive long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<ResidenciaSaida> cadastrar(@RequestBody @Valid ResidenciaEntrada entrada) {
        var saida=service.cadastrar(entrada);
        URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(saida.getCdResidencia()).toUri();
        return ResponseEntity.created(location).body(saida);
    }
    @PutMapping("/{id}") public ResidenciaSaida atualizar(@PathVariable @Positive long id,@RequestBody @Valid ResidenciaEntrada entrada) { return service.atualizar(id,entrada); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> remover(@PathVariable @Positive long id) { service.remover(id); return ResponseEntity.noContent().build(); }
}
