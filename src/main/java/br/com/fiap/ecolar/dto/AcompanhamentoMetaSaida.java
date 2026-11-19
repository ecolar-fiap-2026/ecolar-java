package br.com.fiap.ecolar.dto;
import java.math.BigDecimal;
public record AcompanhamentoMetaSaida(long cdMeta, BigDecimal limiteKwh, BigDecimal saldoKwh,
                                     String situacao, ResumoSaida consumo) {}
