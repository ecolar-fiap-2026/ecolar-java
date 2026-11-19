package br.com.fiap.ecolar.dto;
import java.time.OffsetDateTime;
import java.util.Map;
public record ErroSaida(OffsetDateTime timestamp,int status,String codigo,String mensagem,String caminho,Map<String,String> campos) {}
