package br.com.fiap.ecolar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ResidenciaSaida {
    private Long cdResidencia;
    private String nmResidencia;
    private Integer qtMoradores;
    private BigDecimal vlTarifaKwh;

    public Long getCdResidencia() { return cdResidencia; }
    public void setCdResidencia(Long cdResidencia) { this.cdResidencia = cdResidencia; }
    public String getNmResidencia() { return nmResidencia; }
    public void setNmResidencia(String nmResidencia) { this.nmResidencia = nmResidencia; }
    public Integer getQtMoradores() { return qtMoradores; }
    public void setQtMoradores(Integer qtMoradores) { this.qtMoradores = qtMoradores; }
    public BigDecimal getVlTarifaKwh() { return vlTarifaKwh; }
    public void setVlTarifaKwh(BigDecimal vlTarifaKwh) { this.vlTarifaKwh = vlTarifaKwh; }
}
