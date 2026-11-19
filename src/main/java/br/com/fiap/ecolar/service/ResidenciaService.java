package br.com.fiap.ecolar.service;

import br.com.fiap.ecolar.dao.ResidenciaDao;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.exception.*;
import br.com.fiap.ecolar.model.Residencia;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResidenciaService {
    private final ResidenciaDao dao;
    private final ModelMapper mapper;
    public ResidenciaService(ResidenciaDao dao, ModelMapper mapper) { this.dao=dao; this.mapper=mapper; }
    public Residencia entidade(long id) { return dao.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("Residência não encontrada.")); }
    public void bloquear(long id) { if (!dao.bloquear(id)) throw new RecursoNaoEncontradoException("Residência não encontrada."); }
    public List<ResidenciaSaida> listar() { return dao.listar().stream().map(this::saida).toList(); }
    public ResidenciaSaida buscar(long id) { return saida(entidade(id)); }
    @Transactional public ResidenciaSaida cadastrar(ResidenciaEntrada entrada) {
        Residencia m = mapper.map(entrada, Residencia.class); m.setNmResidencia(m.getNmResidencia().trim());
        return buscar(dao.inserir(m));
    }
    @Transactional public ResidenciaSaida atualizar(long id, ResidenciaEntrada entrada) {
        bloquear(id);
        Residencia m = mapper.map(entrada, Residencia.class); m.setNmResidencia(m.getNmResidencia().trim());
        if (!dao.atualizar(id,m)) throw new RecursoNaoEncontradoException("Residência não encontrada.");
        return buscar(id);
    }
    @Transactional public void remover(long id) {
        bloquear(id);
        if (dao.possuiDependentes(id)) throw new RegraNegocioException("Residência possui consumos, metas, aparelhos ou resultados de IA vinculados. Remoção bloqueada.");
        if (!dao.remover(id)) throw new RecursoNaoEncontradoException("Residência não encontrada.");
    }
    private ResidenciaSaida saida(Residencia m) { return mapper.map(m,ResidenciaSaida.class); }
}
