package br.com.fiap.ecolar.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConsumoDiario {
    private Long cdConsumo;
    private Long cdResidencia;
    private LocalDate dtConsumo;
    private BigDecimal vlConsumoKwh;
    private String dsOrigem;

    public Long getCdConsumo() { return cdConsumo; }
    public void setCdConsumo(Long cdConsumo) { this.cdConsumo = cdConsumo; }
    public Long getCdResidencia() { return cdResidencia; }
    public void setCdResidencia(Long cdResidencia) { this.cdResidencia = cdResidencia; }
    public LocalDate getDtConsumo() { return dtConsumo; }
    public void setDtConsumo(LocalDate dtConsumo) { this.dtConsumo = dtConsumo; }
    public BigDecimal getVlConsumoKwh() { return vlConsumoKwh; }
    public void setVlConsumoKwh(BigDecimal vlConsumoKwh) { this.vlConsumoKwh = vlConsumoKwh; }
    public String getDsOrigem() { return dsOrigem; }
    public void setDsOrigem(String dsOrigem) { this.dsOrigem = dsOrigem; }
}
