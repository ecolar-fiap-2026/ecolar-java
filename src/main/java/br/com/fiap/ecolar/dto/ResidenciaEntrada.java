package br.com.fiap.ecolar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class ResidenciaEntrada {
    @NotBlank @Size(max=100)
    private String nmResidencia;
    @NotNull @Min(1) @Max(9999)
    private Integer qtMoradores;
    @NotNull @DecimalMin("0.0") @Digits(integer=6, fraction=4)
    private BigDecimal vlTarifaKwh;

    public String getNmResidencia() { return nmResidencia; }
    public void setNmResidencia(String nmResidencia) { this.nmResidencia = nmResidencia; }
    public Integer getQtMoradores() { return qtMoradores; }
    public void setQtMoradores(Integer qtMoradores) { this.qtMoradores = qtMoradores; }
    public BigDecimal getVlTarifaKwh() { return vlTarifaKwh; }
    public void setVlTarifaKwh(BigDecimal vlTarifaKwh) { this.vlTarifaKwh = vlTarifaKwh; }
}
