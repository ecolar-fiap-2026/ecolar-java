package br.com.fiap.ecolar.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record IaEntrada(@NotNull @PastOrPresent LocalDate dataReferencia) {}
