package br.com.fiap.ecolar.service;
import br.com.fiap.ecolar.dao.ConsumoDiarioDao;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.exception.EntradaInvalidaException;
import java.math.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResumoService {
    private final ResidenciaService residencias; private final ConsumoDiarioDao consumos; private final MetaConsumoService metas;
    public ResumoService(ResidenciaService r, ConsumoDiarioDao c, MetaConsumoService m) { residencias=r; consumos=c; metas=m; }
    @Transactional public ResumoSaida resumo(long residencia,LocalDate inicio,LocalDate fim) {
        if (inicio==null || fim==null || fim.isBefore(inicio)) throw new EntradaInvalidaException("Informe início e fim válidos, com fim igual ou posterior ao início.");
        long dias=ChronoUnit.DAYS.between(inicio,fim)+1;
        if (dias>3660) throw new EntradaInvalidaException("Consulte no máximo 3660 dias por vez.");
        residencias.bloquear(residencia);
        var casa=residencias.entidade(residencia); var registros=consumos.periodo(residencia,inicio,fim);
        BigDecimal total=registros.stream().map(c -> c.getVlConsumoKwh()).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal media=registros.isEmpty()?null:total.divide(BigDecimal.valueOf(registros.size()),3,RoundingMode.HALF_UP);
        BigDecimal cobertura=BigDecimal.valueOf(registros.size()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(dias),2,RoundingMode.HALF_UP);
        return new ResumoSaida(residencia,inicio,fim,dias,registros.size(),cobertura,total.setScale(3,RoundingMode.HALF_UP),media,
                total.multiply(casa.getVlTarifaKwh()).setScale(2,RoundingMode.HALF_UP),
                "Dias ausentes não recebem zero. Custo usa tarifa atual informada, sem tributos ou componentes fixos da fatura.");
    }
    @Transactional public AcompanhamentoMetaSaida acompanhar(long id) {
        var meta=metas.entidade(id); var r=resumo(meta.getCdResidencia(),meta.getDtInicio(),meta.getDtFim());
        int cmp=r.totalKwh().compareTo(meta.getVlLimiteKwh());
        String estado=r.diasRegistrados()==0?"SEM DADOS":cmp>0?"LIMITE ULTRAPASSADO":r.diasRegistrados()<r.diasPeriodo()?"PARCIAL":cmp==0?"NO LIMITE":"DENTRO DO LIMITE";
        return new AcompanhamentoMetaSaida(id,meta.getVlLimiteKwh(),meta.getVlLimiteKwh().subtract(r.totalKwh()),estado,r);
    }
}
