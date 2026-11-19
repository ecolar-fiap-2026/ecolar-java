package br.com.fiap.ecolar.exception;
public class PersistenciaException extends RuntimeException {
    private final boolean indisponivel;
    public PersistenciaException(boolean indisponivel, Throwable causa) { super("Falha de persistência.", causa); this.indisponivel = indisponivel; }
    public boolean isIndisponivel() { return indisponivel; }
}
