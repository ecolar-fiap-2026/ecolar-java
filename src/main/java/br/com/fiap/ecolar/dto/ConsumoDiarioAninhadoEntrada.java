package br.com.fiap.ecolar.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class ConsumoDiarioAninhadoEntrada {
    @NotNull @PastOrPresent
    private LocalDate dtConsumo;
    @NotNull @DecimalMin("0.0") @DecimalMax("10000.000") @Digits(integer=9, fraction=3)
    private BigDecimal vlConsumoKwh;
    @NotBlank @Pattern(regexp="MANUAL|IMPORTADO|SIMULADO")
    private String dsOrigem;

    public LocalDate getDtConsumo() { return dtConsumo; }
    public void setDtConsumo(LocalDate dtConsumo) { this.dtConsumo = dtConsumo; }
    public BigDecimal getVlConsumoKwh() { return vlConsumoKwh; }
    public void setVlConsumoKwh(BigDecimal vlConsumoKwh) { this.vlConsumoKwh = vlConsumoKwh; }
    public String getDsOrigem() { return dsOrigem; }
    public void setDsOrigem(String dsOrigem) { this.dsOrigem = dsOrigem; }
}
