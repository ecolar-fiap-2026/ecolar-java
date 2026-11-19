package br.com.fiap.ecolar.controller;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class RelacionamentosController {
    private final ConsumoDiarioService consumos; private final MetaConsumoService metas; private final ResumoService resumo; private final ModelMapper mapper;
    public RelacionamentosController(ConsumoDiarioService c,MetaConsumoService m,ResumoService r,ModelMapper mapper) { consumos=c; metas=m; resumo=r; this.mapper=mapper; }
    @GetMapping("/residencias/{id}/consumos") public List<ConsumoDiarioSaida> consumos(@PathVariable @Positive long id) { return consumos.listarPorResidencia(id); }
    @PostMapping("/residencias/{id}/consumos") public ResponseEntity<ConsumoDiarioSaida> consumo(@PathVariable @Positive long id,@RequestBody @Valid ConsumoDiarioAninhadoEntrada entrada) {
        var d=mapper.map(entrada,ConsumoDiarioEntrada.class); d.setCdResidencia(id); var saida=consumos.cadastrar(d);
        URI location=ServletUriComponentsBuilder.fromCurrentContextPath().path("/consumos/{id}").buildAndExpand(saida.getCdConsumo()).toUri();
        return ResponseEntity.created(location).body(saida);
    }
    @GetMapping("/residencias/{id}/metas") public List<MetaConsumoSaida> metas(@PathVariable @Positive long id) { return metas.listarPorResidencia(id); }
    @PostMapping("/residencias/{id}/metas") public ResponseEntity<MetaConsumoSaida> meta(@PathVariable @Positive long id,@RequestBody @Valid MetaConsumoAninhadoEntrada entrada) {
        var d=mapper.map(entrada,MetaConsumoEntrada.class); d.setCdResidencia(id); var saida=metas.cadastrar(d);
        URI location=ServletUriComponentsBuilder.fromCurrentContextPath().path("/metas/{id}").buildAndExpand(saida.getCdMeta()).toUri();
        return ResponseEntity.created(location).body(saida);
    }
    @GetMapping("/residencias/{id}/resumo") public ResumoSaida resumo(@PathVariable @Positive long id,@RequestParam LocalDate inicio,@RequestParam LocalDate fim) { return resumo.resumo(id,inicio,fim); }
    @GetMapping("/metas/{id}/acompanhamento") public AcompanhamentoMetaSaida acompanhamento(@PathVariable @Positive long id) { return resumo.acompanhar(id); }
}
