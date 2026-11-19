package br.com.fiap.ecolar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class MetaConsumoEntrada {
    @NotNull @Positive @Max(9999999999L)
    private Long cdResidencia;
    @NotNull
    private LocalDate dtInicio;
    @NotNull
    private LocalDate dtFim;
    @NotNull @DecimalMin(value="0.0", inclusive=false) @Digits(integer=9, fraction=3)
    private BigDecimal vlLimiteKwh;

    public Long getCdResidencia() { return cdResidencia; }
    public void setCdResidencia(Long cdResidencia) { this.cdResidencia = cdResidencia; }
    public LocalDate getDtInicio() { return dtInicio; }
    public void setDtInicio(LocalDate dtInicio) { this.dtInicio = dtInicio; }
    public LocalDate getDtFim() { return dtFim; }
    public void setDtFim(LocalDate dtFim) { this.dtFim = dtFim; }
    public BigDecimal getVlLimiteKwh() { return vlLimiteKwh; }
    public void setVlLimiteKwh(BigDecimal vlLimiteKwh) { this.vlLimiteKwh = vlLimiteKwh; }
}
