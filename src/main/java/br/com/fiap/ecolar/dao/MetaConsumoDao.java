package br.com.fiap.ecolar.dao;

import br.com.fiap.ecolar.model.MetaConsumo;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class MetaConsumoDao extends JdbcSupport {
    public MetaConsumoDao(DataSource dataSource) { super(dataSource); }
    private MetaConsumo ler(ResultSet rs) throws SQLException {
        MetaConsumo m = new MetaConsumo();
        m.setCdMeta(rs.getLong("cd_meta"));
        m.setCdResidencia(rs.getLong("cd_residencia"));
        m.setDtInicio(rs.getDate("dt_inicio").toLocalDate());
        m.setDtFim(rs.getDate("dt_fim").toLocalDate());
        m.setVlLimiteKwh(rs.getBigDecimal("vl_limite_kwh"));
        return m;
    }
    public List<MetaConsumo> listar() { return consultar("SELECT * FROM tb_meta_consumo ORDER BY cd_meta", this::ler); }
    public Optional<MetaConsumo> buscar(long id) { return consultar("SELECT * FROM tb_meta_consumo WHERE cd_meta=?", this::ler, id).stream().findFirst(); }
    public long inserir(MetaConsumo m) {
        long id = proximoId("sq_tb_meta_consumo");
        executar("INSERT INTO tb_meta_consumo (cd_meta,cd_residencia,dt_inicio,dt_fim,vl_limite_kwh) VALUES (?,?,?,?,?)", id, m.getCdResidencia(), m.getDtInicio(), m.getDtFim(), m.getVlLimiteKwh());
        return id;
    }
    public boolean atualizar(long id, MetaConsumo m) {
        return executar("UPDATE tb_meta_consumo SET cd_residencia=?,dt_inicio=?,dt_fim=?,vl_limite_kwh=? WHERE cd_meta=?", m.getCdResidencia(), m.getDtInicio(), m.getDtFim(), m.getVlLimiteKwh(), id) == 1;
    }
    public boolean remover(long id) { return executar("DELETE FROM tb_meta_consumo WHERE cd_meta=?", id) == 1; }
    public List<MetaConsumo> listarPorResidencia(long id) { return consultar("SELECT * FROM tb_meta_consumo WHERE cd_residencia=? ORDER BY dt_inicio", this::ler, id); }
    public boolean sobrepoe(long residencia, java.time.LocalDate inicio, java.time.LocalDate fim, long ignorarId) { return consultar("SELECT COUNT(*) FROM tb_meta_consumo WHERE cd_residencia=? AND dt_inicio<=? AND dt_fim>=? AND cd_meta<>?", r -> r.getLong(1), residencia, fim, inicio, ignorarId).getFirst()>0; }
}
