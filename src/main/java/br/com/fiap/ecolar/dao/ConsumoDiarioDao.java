package br.com.fiap.ecolar.dao;

import br.com.fiap.ecolar.model.ConsumoDiario;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class ConsumoDiarioDao extends JdbcSupport {
    public ConsumoDiarioDao(DataSource dataSource) { super(dataSource); }
    private ConsumoDiario ler(ResultSet rs) throws SQLException {
        ConsumoDiario m = new ConsumoDiario();
        m.setCdConsumo(rs.getLong("cd_consumo"));
        m.setCdResidencia(rs.getLong("cd_residencia"));
        m.setDtConsumo(rs.getDate("dt_consumo").toLocalDate());
        m.setVlConsumoKwh(rs.getBigDecimal("vl_consumo_kwh"));
        m.setDsOrigem(rs.getString("ds_origem"));
        return m;
    }
    public List<ConsumoDiario> listar() { return consultar("SELECT * FROM tb_consumo_diario ORDER BY cd_consumo", this::ler); }
    public Optional<ConsumoDiario> buscar(long id) { return consultar("SELECT * FROM tb_consumo_diario WHERE cd_consumo=?", this::ler, id).stream().findFirst(); }
    public long inserir(ConsumoDiario m) {
        long id = proximoId("sq_tb_consumo_diario");
        executar("INSERT INTO tb_consumo_diario (cd_consumo,cd_residencia,dt_consumo,vl_consumo_kwh,ds_origem) VALUES (?,?,?,?,?)", id, m.getCdResidencia(), m.getDtConsumo(), m.getVlConsumoKwh(), m.getDsOrigem());
        return id;
    }
    public boolean atualizar(long id, ConsumoDiario m) {
        return executar("UPDATE tb_consumo_diario SET cd_residencia=?,dt_consumo=?,vl_consumo_kwh=?,ds_origem=? WHERE cd_consumo=?", m.getCdResidencia(), m.getDtConsumo(), m.getVlConsumoKwh(), m.getDsOrigem(), id) == 1;
    }
    public boolean remover(long id) { return executar("DELETE FROM tb_consumo_diario WHERE cd_consumo=?", id) == 1; }
    public List<ConsumoDiario> listarPorResidencia(long id) { return consultar("SELECT * FROM tb_consumo_diario WHERE cd_residencia=? ORDER BY dt_consumo", this::ler, id); }
    public List<ConsumoDiario> periodo(long id, java.time.LocalDate inicio, java.time.LocalDate fim) { return consultar("SELECT * FROM tb_consumo_diario WHERE cd_residencia=? AND dt_consumo BETWEEN ? AND ? ORDER BY dt_consumo", this::ler, id, inicio, fim); }
    public boolean existeNoDia(long residencia, java.time.LocalDate data, long ignorarId) { return consultar("SELECT COUNT(*) FROM tb_consumo_diario WHERE cd_residencia=? AND dt_consumo=? AND cd_consumo<>?", r -> r.getLong(1), residencia, data, ignorarId).getFirst()>0; }
}
