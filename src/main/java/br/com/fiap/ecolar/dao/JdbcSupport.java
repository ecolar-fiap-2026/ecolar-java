package br.com.fiap.ecolar.dao;

import br.com.fiap.ecolar.exception.*;
import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

/** PreparedStatement em todas as operações e mesma conexão nas transações Spring. */
public abstract class JdbcSupport {
    private final DataSource dataSource;
    protected JdbcSupport(DataSource dataSource) { this.dataSource = dataSource; }
    @FunctionalInterface protected interface Leitor<T> { T ler(ResultSet rs) throws SQLException; }
    @FunctionalInterface private interface Operacao<T> { T executar(Connection c) throws SQLException; }
    private <T> T conexao(Operacao<T> operacao) {
        Connection c = null;
        try { c = DataSourceUtils.getConnection(dataSource); return operacao.executar(c); }
        catch (CannotGetJdbcConnectionException e) { throw new PersistenciaException(true, e); }
        catch (SQLException e) {
            int codigo = e.getErrorCode(); String estado = e.getSQLState();
            if (codigo == 1 || codigo == 2292 || codigo == 2291 || "23505".equals(estado) || "23503".equals(estado) || "23506".equals(estado))
                throw new RegraNegocioException("Conflito: registro duplicado ou vínculo com outro registro.");
            boolean indisponivel = e instanceof SQLRecoverableException || e instanceof SQLTransientConnectionException
                || e instanceof SQLNonTransientConnectionException || (estado != null && estado.startsWith("08"));
            throw new PersistenciaException(indisponivel, e);
        } finally { if (c != null) DataSourceUtils.releaseConnection(c, dataSource); }
    }
    private void vincular(PreparedStatement ps, Object... valores) throws SQLException {
        for (int i=0; i<valores.length; i++) {
            Object v = valores[i];
            if (v instanceof java.time.LocalDate data) ps.setDate(i+1, java.sql.Date.valueOf(data));
            else if (v instanceof java.time.LocalDateTime dataHora) ps.setTimestamp(i+1, Timestamp.valueOf(dataHora));
            else ps.setObject(i+1, v);
        }
    }
    protected <T> List<T> consultar(String sql, Leitor<T> leitor, Object... valores) {
        return conexao(c -> {
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setQueryTimeout(15); vincular(ps, valores);
                try (ResultSet rs = ps.executeQuery()) {
                    List<T> lista = new ArrayList<>();
                    while (rs.next()) lista.add(leitor.ler(rs));
                    return lista;
                }
            }
        });
    }
    protected int executar(String sql, Object... valores) {
        return conexao(c -> { try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setQueryTimeout(15); vincular(ps, valores); return ps.executeUpdate();
        }});
    }
    protected long proximoId(String sequence) {
        // Nomes internos fixos, nunca recebidos da requisição.
        if (!Set.of("sq_tb_residencia","sq_tb_consumo_diario","sq_tb_meta_consumo","sq_tb_previsao_consumo","sq_tb_analise_padrao").contains(sequence))
            throw new IllegalArgumentException("Sequence interna inválida.");
        return consultar("SELECT " + sequence + ".NEXTVAL FROM dual", r -> r.getLong(1)).getFirst();
    }
    public boolean testarConexao() { return consultar("SELECT 1 FROM dual", r -> r.getInt(1)).getFirst() == 1; }
}
