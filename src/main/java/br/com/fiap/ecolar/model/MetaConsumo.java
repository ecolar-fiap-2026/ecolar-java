package br.com.fiap.ecolar.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MetaConsumo {
    private Long cdMeta;
    private Long cdResidencia;
    private LocalDate dtInicio;
    private LocalDate dtFim;
    private BigDecimal vlLimiteKwh;

    public Long getCdMeta() { return cdMeta; }
    public void setCdMeta(Long cdMeta) { this.cdMeta = cdMeta; }
    public Long getCdResidencia() { return cdResidencia; }
    public void setCdResidencia(Long cdResidencia) { this.cdResidencia = cdResidencia; }
    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }
    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    public BigDecimal getVlLimiteKwh() { return vlLimiteKwh; }
    public void setVlLimiteKwh(BigDecimal vlLimiteKwh) { this.vlLimiteKwh = vlLimiteKwh; }
}
