package br.com.fiap.ecolar.service;

import br.com.fiap.ecolar.dao.ConsumoDiarioDao;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.exception.*;
import br.com.fiap.ecolar.model.ConsumoDiario;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsumoDiarioService {
    private final ConsumoDiarioDao dao; private final ResidenciaService residencias;
    private final ModelMapper mapper; private final Clock clock;
    public ConsumoDiarioService(ConsumoDiarioDao dao, ResidenciaService residencias, ModelMapper mapper, Clock clock) {
        this.dao=dao; this.residencias=residencias; this.mapper=mapper; this.clock=clock;
    }
    public ConsumoDiario entidade(long id) { return dao.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Consumo não encontrado.")); }
    public List<ConsumoDiarioSaida> listar() { return dao.listar().stream().map(this::saida).toList(); }
    public List<ConsumoDiarioSaida> listarPorResidencia(long id) { residencias.entidade(id); return dao.listarPorResidencia(id).stream().map(this::saida).toList(); }
    public ConsumoDiarioSaida buscar(long id) { return saida(entidade(id)); }
    private void validar(ConsumoDiarioEntrada e, long ignorar) {
        if (e.getDtConsumo().isAfter(LocalDate.now(clock))) throw new EntradaInvalidaException("Consumo diário não pode ter data futura.");
        if (dao.existeNoDia(e.getCdResidencia(),e.getDtConsumo(),ignorar)) throw new RegraNegocioException("Já existe consumo nessa data para a residência. Edite o registro existente.");
    }
    @Transactional public ConsumoDiarioSaida cadastrar(ConsumoDiarioEntrada e) {
        residencias.bloquear(e.getCdResidencia()); validar(e,0);
        return buscar(dao.inserir(mapper.map(e,ConsumoDiario.class)));
    }
    @Transactional public ConsumoDiarioSaida atualizar(long id, ConsumoDiarioEntrada e) {
        ConsumoDiario atual=entidade(id); residencias.bloquear(atual.getCdResidencia());
        if (!atual.getCdResidencia().equals(e.getCdResidencia())) throw new RegraNegocioException("Um consumo não pode ser transferido para outra residência.");
        validar(e,id);
        if (!dao.atualizar(id,mapper.map(e,ConsumoDiario.class))) throw new RecursoNaoEncontradoException("Consumo não encontrado.");
        return buscar(id);
    }
    @Transactional public void remover(long id) {
        ConsumoDiario atual=entidade(id); residencias.bloquear(atual.getCdResidencia());
        if (!dao.remover(id)) throw new RecursoNaoEncontradoException("Consumo não encontrado.");
    }
    private ConsumoDiarioSaida saida(ConsumoDiario m) { return mapper.map(m,ConsumoDiarioSaida.class); }
}
