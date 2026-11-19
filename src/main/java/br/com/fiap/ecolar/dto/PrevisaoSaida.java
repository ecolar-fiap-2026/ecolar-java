package br.com.fiap.ecolar.dto;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
public record PrevisaoSaida(long cdPrevisao,long cdResidencia,LocalDate dtReferencia,LocalDate dtAlvo,
        BigDecimal vlPrevistoKwh,String dsModelo,LocalDateTime dtGeracao,String dsOrigem,List<String> avisos) {}
