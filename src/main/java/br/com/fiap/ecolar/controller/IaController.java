package br.com.fiap.ecolar.controller;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.service.IaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class IaController {
    private final IaService service;
    public IaController(IaService service) { this.service=service; }
    @PostMapping("/residencias/{id}/previsoes") public ResponseEntity<PrevisaoSaida> prever(@PathVariable @Positive long id,@RequestBody @Valid IaEntrada e) {
        var p=service.prever(id,e.dataReferencia()); URI location=ServletUriComponentsBuilder.fromCurrentContextPath().path("/previsoes/{id}").buildAndExpand(p.cdPrevisao()).toUri();
        return ResponseEntity.created(location).body(p);
    }
    @PostMapping("/residencias/{id}/analises") public ResponseEntity<AnalisePadraoSaida> agrupar(@PathVariable @Positive long id,@RequestBody @Valid IaEntrada e) {
        var a=service.agrupar(id,e.dataReferencia()); URI location=ServletUriComponentsBuilder.fromCurrentContextPath().path("/analises/{id}").buildAndExpand(a.cdAnalise()).toUri();
        return ResponseEntity.created(location).body(a);
    }
    @GetMapping("/residencias/{id}/previsoes") public List<PrevisaoSaida> previsoes(@PathVariable @Positive long id) { return service.previsoes(id); }
    @GetMapping("/residencias/{id}/analises") public List<AnalisePadraoSaida> analises(@PathVariable @Positive long id) { return service.analises(id); }
    @GetMapping("/previsoes/{id}") public PrevisaoSaida previsao(@PathVariable @Positive long id) { return service.previsao(id); }
    @GetMapping("/analises/{id}") public AnalisePadraoSaida analise(@PathVariable @Positive long id) { return service.analise(id); }
}
