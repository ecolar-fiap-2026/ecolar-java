package br.com.fiap.ecolar.service;

import br.com.fiap.ecolar.dao.MetaConsumoDao;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.exception.*;
import br.com.fiap.ecolar.model.MetaConsumo;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MetaConsumoService {
    private final MetaConsumoDao dao; private final ResidenciaService residencias; private final ModelMapper mapper;
    public MetaConsumoService(MetaConsumoDao dao, ResidenciaService residencias, ModelMapper mapper) { this.dao=dao; this.residencias=residencias; this.mapper=mapper; }
    public MetaConsumo entidade(long id) { return dao.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Meta não encontrada.")); }
    public List<MetaConsumoSaida> listar() { return dao.listar().stream().map(this::saida).toList(); }
    public List<MetaConsumoSaida> listarPorResidencia(long id) { residencias.entidade(id); return dao.listarPorResidencia(id).stream().map(this::saida).toList(); }
    public MetaConsumoSaida buscar(long id) { return saida(entidade(id)); }
    private void validar(MetaConsumoEntrada e,long ignorar) {
        if (e.getDtFim().isBefore(e.getDtInicio())) throw new EntradaInvalidaException("Fim da meta deve ser igual ou posterior ao início.");
        if (dao.sobrepoe(e.getCdResidencia(),e.getDtInicio(),e.getDtFim(),ignorar)) throw new RegraNegocioException("Já existe meta sobreposta nesse período para a residência (limites inclusivos).");
    }
    @Transactional public MetaConsumoSaida cadastrar(MetaConsumoEntrada e) {
        residencias.bloquear(e.getCdResidencia()); validar(e,0);
        return buscar(dao.inserir(mapper.map(e,MetaConsumo.class)));
    }
    @Transactional public MetaConsumoSaida atualizar(long id, MetaConsumoEntrada e) {
        MetaConsumo atual=entidade(id); residencias.bloquear(atual.getCdResidencia());
        if (!atual.getCdResidencia().equals(e.getCdResidencia())) throw new RegraNegocioException("Uma meta não pode ser transferida para outra residência.");
        validar(e,id);
        if (!dao.atualizar(id,mapper.map(e,MetaConsumo.class))) throw new RecursoNaoEncontradoException("Meta não encontrada.");
        return buscar(id);
    }
    @Transactional public void remover(long id) {
        MetaConsumo atual=entidade(id); residencias.bloquear(atual.getCdResidencia());
        if (!dao.remover(id)) throw new RecursoNaoEncontradoException("Meta não encontrada.");
    }
    private MetaConsumoSaida saida(MetaConsumo m) { return mapper.map(m,MetaConsumoSaida.class); }
}
