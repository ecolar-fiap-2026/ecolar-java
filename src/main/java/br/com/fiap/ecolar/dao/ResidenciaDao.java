package br.com.fiap.ecolar.dao;

import br.com.fiap.ecolar.model.Residencia;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class ResidenciaDao extends JdbcSupport {
    public ResidenciaDao(DataSource dataSource) { super(dataSource); }
    private Residencia ler(ResultSet rs) throws SQLException {
        Residencia m = new Residencia();
        m.setCdResidencia(rs.getLong("cd_residencia"));
        m.setNmResidencia(rs.getString("nm_residencia"));
        m.setQtMoradores(rs.getInt("qt_moradores"));
        m.setVlTarifaKwh(rs.getBigDecimal("vl_tarifa_kwh"));
        return m;
    }
    public List<Residencia> listar() { return consultar("SELECT * FROM tb_residencia ORDER BY cd_residencia", this::ler); }
    public Optional<Residencia> buscar(long id) { return consultar("SELECT * FROM tb_residencia WHERE cd_residencia=?", this::ler, id).stream().findFirst(); }
    public long inserir(Residencia m) {
        long id = proximoId("sq_tb_residencia");
        executar("INSERT INTO tb_residencia (cd_residencia,nm_residencia,qt_moradores,vl_tarifa_kwh) VALUES (?,?,?,?)", id, m.getNmResidencia(), m.getQtMoradores(), m.getVlTarifaKwh());
        return id;
    }
    public boolean atualizar(long id, Residencia m) {
        return executar("UPDATE tb_residencia SET nm_residencia=?,qt_moradores=?,vl_tarifa_kwh=? WHERE cd_residencia=?", m.getNmResidencia(), m.getQtMoradores(), m.getVlTarifaKwh(), id) == 1;
    }
    public boolean remover(long id) { return executar("DELETE FROM tb_residencia WHERE cd_residencia=?", id) == 1; }
    public boolean bloquear(long id) { return !consultar("SELECT cd_residencia FROM tb_residencia WHERE cd_residencia=? FOR UPDATE", r -> r.getLong(1), id).isEmpty(); }
    public boolean possuiDependentes(long id) {
        for (String tabela : List.of("tb_consumo_diario","tb_meta_consumo","tb_residencia_aparelho","tb_previsao_consumo","tb_analise_padrao"))
            if (consultar("SELECT COUNT(*) FROM " + tabela + " WHERE cd_residencia=?", r -> r.getLong(1), id).getFirst() > 0) return true;
        return false;
    }
}
