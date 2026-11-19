package br.com.fiap.ecolar.dto;
import java.time.LocalDate;
import java.util.List;
public record AnalisePadraoSaida(long cdAnalise,long cdResidencia,LocalDate dtInicio,LocalDate dtFim,
        int cdGrupo,String dsInterpretacao,String dsModelo,String dsOrigem,List<String> avisos) {}
