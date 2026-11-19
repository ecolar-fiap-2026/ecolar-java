package br.com.fiap.ecolar.service;
import br.com.fiap.ecolar.dao.*;
import br.com.fiap.ecolar.dto.*;
import br.com.fiap.ecolar.exception.*;
import br.com.fiap.ecolar.model.ConsumoDiario;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IaService {
    private final ResidenciaService residencias; private final ConsumoDiarioDao consumos;
    private final IaRegistroDao registros; private final IaClient client; private final Clock clock;
    public IaService(ResidenciaService r,ConsumoDiarioDao c,IaRegistroDao i,IaClient client,Clock clock) { residencias=r; consumos=c; registros=i; this.client=client; this.clock=clock; }
    private List<ConsumoDiario> historico(long id,LocalDate ref) {
        if (ref==null || ref.isAfter(LocalDate.now(clock))) throw new EntradaInvalidaException("Data de referência inválida ou futura.");
        residencias.bloquear(id); var h=consumos.periodo(id,ref.minusDays(6),ref);
        if (h.size()!=7) throw new RegraNegocioException("A análise precisa de sete dias consecutivos de consumo dessa residência.");
        for(int j=0;j<7;j++) if(!h.get(j).getDtConsumo().equals(ref.minusDays(6-j))) throw new RegraNegocioException("Histórico com lacunas. Complete os sete dias consecutivos.");
        return h;
    }
    private IaRequisicao requisicao(List<ConsumoDiario> h) { return new IaRequisicao(h.stream().map(c -> new IaHistoricoDto(c.getDtConsumo(),c.getVlConsumoKwh())).toList()); }
    private String texto(JsonNode j,String chave,int max) {
        JsonNode n=j.get(chave);
        if(n==null || !n.isTextual() || n.asText().isBlank() || n.asText().length()>max) throw invalida();
        return n.asText();
    }
    private IaException invalida() { return new IaException(502,"Saída do modelo inválida. Nenhum resultado foi gravado."); }
    private String modelo(JsonNode j) {
        if(!"MODELO".equals(texto(j,"origem",10))) throw invalida();
        return texto(j,"modelo",80);
    }
    private void dataIgual(JsonNode j,String campo,LocalDate esperado) {
        try { if(!LocalDate.parse(texto(j,campo,10)).equals(esperado)) throw invalida(); }
        catch(java.time.format.DateTimeParseException e) { throw invalida(); }
    }
    private List<String> avisos(JsonNode j,List<ConsumoDiario> h) {
        JsonNode a=j.get("avisos"); if(a==null || !a.isArray() || a.size()>20) throw invalida();
        List<String> lista=new ArrayList<>();
        for(JsonNode n:a) { if(!n.isTextual() || n.asText().length()>1000) throw invalida(); lista.add(n.asText()); }
        if(h.stream().anyMatch(c -> "SIMULADO".equals(c.getDsOrigem()))) lista.add("Esta inferência utiliza registros de entrada SIMULADO; não são medições reais da residência.");
        return List.copyOf(lista);
    }
    @Transactional public PrevisaoSaida prever(long id,LocalDate ref) {
        var h=historico(id,ref); JsonNode j=client.chamar("prever",requisicao(h)); String modelo=modelo(j);
        dataIgual(j,"data_referencia",ref); dataIgual(j,"data_alvo",ref.plusDays(1));
        JsonNode numero=j.get("consumo_previsto_kwh"); if(numero==null || !numero.isNumber()) throw invalida();
        BigDecimal valor;
        try { valor=numero.decimalValue(); } catch(RuntimeException e) { throw invalida(); }
        if(valor.signum()<0 || valor.compareTo(new BigDecimal("999999999.999"))>0) throw invalida();
        var avisos=avisos(j,h); var geracao=LocalDateTime.now(clock);
        long cd=registros.inserirPrevisao(id,ref,ref.plusDays(1),valor.setScale(3,RoundingMode.HALF_UP),modelo,geracao);
        var p=previsao(cd);
        return new PrevisaoSaida(p.cdPrevisao(),id,p.dtReferencia(),p.dtAlvo(),p.vlPrevistoKwh(),p.dsModelo(),p.dtGeracao(),p.dsOrigem(),avisos);
    }
    @Transactional public AnalisePadraoSaida agrupar(long id,LocalDate ref) {
        var h=historico(id,ref); JsonNode j=client.chamar("agrupar",requisicao(h)); String modelo=modelo(j);
        dataIgual(j,"data_inicio",ref.minusDays(6)); dataIgual(j,"data_fim",ref);
        JsonNode grupo=j.get("grupo"); if(grupo==null || !grupo.isIntegralNumber() || !grupo.canConvertToInt() || grupo.intValue()<0 || grupo.intValue()>9999) throw invalida();
        String interpretacao=texto(j,"nome",200)+". "+texto(j,"interpretacao",500);
        if(interpretacao.length()>500) throw invalida();
        var avisos=avisos(j,h);
        long cd=registros.inserirAnalise(id,ref.minusDays(6),ref,grupo.intValue(),interpretacao,modelo);
        var a=analise(cd);
        return new AnalisePadraoSaida(a.cdAnalise(),id,a.dtInicio(),a.dtFim(),a.cdGrupo(),a.dsInterpretacao(),a.dsModelo(),a.dsOrigem(),avisos);
    }
    public PrevisaoSaida previsao(long id) { return registros.previsao(id).orElseThrow(() -> new RecursoNaoEncontradoException("Previsão não encontrada.")); }
    public AnalisePadraoSaida analise(long id) { return registros.analise(id).orElseThrow(() -> new RecursoNaoEncontradoException("Análise não encontrada.")); }
    public List<PrevisaoSaida> previsoes(long id) { residencias.entidade(id); return registros.previsoes(id); }
    public List<AnalisePadraoSaida> analises(long id) { residencias.entidade(id); return registros.analises(id); }
}
