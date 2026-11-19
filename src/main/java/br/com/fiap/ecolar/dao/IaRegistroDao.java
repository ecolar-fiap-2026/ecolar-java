package br.com.fiap.ecolar.dao;
import br.com.fiap.ecolar.dto.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

/** Histórico imutável de inferências: inserir e consultar, sem alterar fixtures existentes. */
@Repository
public class IaRegistroDao extends JdbcSupport {
    private static final List<String> AVISOS=List.of("Modelo de uma casa francesa sem validação no Brasil. O esquema Oracle não armazena a lista completa de avisos da resposta original.");
    public IaRegistroDao(DataSource ds) { super(ds); }
    private PrevisaoSaida lerPrevisao(ResultSet r) throws SQLException {
        return new PrevisaoSaida(r.getLong("cd_previsao"),r.getLong("cd_residencia"),r.getDate("dt_referencia").toLocalDate(),
            r.getDate("dt_alvo").toLocalDate(),r.getBigDecimal("vl_previsto_kwh"),r.getString("ds_modelo"),r.getTimestamp("dt_geracao").toLocalDateTime(),r.getString("ds_origem"),AVISOS);
    }
    private AnalisePadraoSaida lerAnalise(ResultSet r) throws SQLException {
        return new AnalisePadraoSaida(r.getLong("cd_analise"),r.getLong("cd_residencia"),r.getDate("dt_inicio").toLocalDate(),
            r.getDate("dt_fim").toLocalDate(),r.getInt("cd_grupo"),r.getString("ds_interpretacao"),r.getString("ds_modelo"),r.getString("ds_origem"),AVISOS);
    }
    public Optional<PrevisaoSaida> previsao(long id) { return consultar("SELECT * FROM tb_previsao_consumo WHERE cd_previsao=?",this::lerPrevisao,id).stream().findFirst(); }
    public Optional<AnalisePadraoSaida> analise(long id) { return consultar("SELECT * FROM tb_analise_padrao WHERE cd_analise=?",this::lerAnalise,id).stream().findFirst(); }
    public List<PrevisaoSaida> previsoes(long residencia) { return consultar("SELECT * FROM tb_previsao_consumo WHERE cd_residencia=? ORDER BY cd_previsao",this::lerPrevisao,residencia); }
    public List<AnalisePadraoSaida> analises(long residencia) { return consultar("SELECT * FROM tb_analise_padrao WHERE cd_residencia=? ORDER BY cd_analise",this::lerAnalise,residencia); }
    public long inserirPrevisao(long casa,LocalDate referencia,LocalDate alvo,BigDecimal valor,String modelo,LocalDateTime geracao) {
        long id=proximoId("sq_tb_previsao_consumo");
        executar("INSERT INTO tb_previsao_consumo (cd_previsao,cd_residencia,dt_referencia,dt_alvo,vl_previsto_kwh,ds_modelo,dt_geracao,ds_origem) VALUES (?,?,?,?,?,?,?,?)",id,casa,referencia,alvo,valor,modelo,geracao,"MODELO");
        return id;
    }
    public long inserirAnalise(long casa,LocalDate inicio,LocalDate fim,int grupo,String interpretacao,String modelo) {
        long id=proximoId("sq_tb_analise_padrao");
        executar("INSERT INTO tb_analise_padrao (cd_analise,cd_residencia,dt_inicio,dt_fim,cd_grupo,ds_interpretacao,ds_modelo,ds_origem) VALUES (?,?,?,?,?,?,?,?)",id,casa,inicio,fim,grupo,interpretacao,modelo,"MODELO");
        return id;
    }
}
