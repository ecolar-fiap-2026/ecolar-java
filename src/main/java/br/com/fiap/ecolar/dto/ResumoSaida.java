package br.com.fiap.ecolar.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record ResumoSaida(long cdResidencia, LocalDate inicio, LocalDate fim, long diasPeriodo,
        int diasRegistrados, BigDecimal coberturaPercentual, BigDecimal totalKwh,
        BigDecimal mediaDiariaKwh, BigDecimal custoEstimadoReais, String observacao) {}
