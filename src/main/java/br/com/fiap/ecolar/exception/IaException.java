package br.com.fiap.ecolar.exception;
public class IaException extends RuntimeException {
    private final int status;
    public IaException(int status, String mensagem) { super(mensagem); this.status = status; }
    public int getStatus() { return status; }
}
