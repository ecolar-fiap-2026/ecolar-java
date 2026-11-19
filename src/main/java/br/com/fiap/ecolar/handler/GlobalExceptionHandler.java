package br.com.fiap.ecolar.handler;
import br.com.fiap.ecolar.dto.ErroSaida;
import br.com.fiap.ecolar.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.transaction.TransactionException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log=LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private ResponseEntity<ErroSaida> resposta(int status,String codigo,String mensagem,HttpServletRequest r,Map<String,String> campos) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(new ErroSaida(OffsetDateTime.now(),status,codigo,mensagem,r.getRequestURI(),campos));
    }
    @ExceptionHandler(RecursoNaoEncontradoException.class) public ResponseEntity<ErroSaida> naoEncontrado(RecursoNaoEncontradoException e,HttpServletRequest r) { return resposta(404,"nao_encontrado",e.getMessage(),r,Map.of()); }
    @ExceptionHandler(RegraNegocioException.class) public ResponseEntity<ErroSaida> conflito(RegraNegocioException e,HttpServletRequest r) { return resposta(409,"conflito",e.getMessage(),r,Map.of()); }
    @ExceptionHandler(EntradaInvalidaException.class) public ResponseEntity<ErroSaida> entrada(EntradaInvalidaException e,HttpServletRequest r) { return resposta(400,"entrada_invalida",e.getMessage(),r,Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<ErroSaida> validacao(MethodArgumentNotValidException e,HttpServletRequest r) {
        Map<String,String> campos=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(f -> campos.putIfAbsent(f.getField(),f.getDefaultMessage()));
        return resposta(400,"validacao","Confira os campos informados.",r,campos);
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,MissingServletRequestParameterException.class,HandlerMethodValidationException.class,ConstraintViolationException.class})
    public ResponseEntity<ErroSaida> formato(Exception e,HttpServletRequest r) { return resposta(400,"entrada_invalida","JSON, parâmetros ou tipos inválidos. Confira o contrato da API.",r,Map.of()); }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) public ResponseEntity<ErroSaida> metodo(HttpRequestMethodNotSupportedException e,HttpServletRequest r) {
        var erro=resposta(405,"metodo_nao_permitido","Método HTTP não permitido para esta rota.",r,Map.of());
        HttpHeaders headers=new HttpHeaders(); headers.putAll(erro.getHeaders()); if(e.getSupportedHttpMethods()!=null) headers.setAllow(e.getSupportedHttpMethods());
        return new ResponseEntity<>(erro.getBody(),headers,HttpStatus.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler(NoResourceFoundException.class) public ResponseEntity<ErroSaida> rota(NoResourceFoundException e,HttpServletRequest r) { return resposta(404,"rota_nao_encontrada","Rota não encontrada.",r,Map.of()); }
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class) public ResponseEntity<ErroSaida> tipo(HttpMediaTypeNotSupportedException e,HttpServletRequest r) { return resposta(415,"tipo_nao_suportado","Use Content-Type: application/json.",r,Map.of()); }
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class) public ResponseEntity<ErroSaida> aceitavel(HttpMediaTypeNotAcceptableException e,HttpServletRequest r) { return resposta(406,"tipo_nao_aceito","A API retorna application/json.",r,Map.of()); }
    @ExceptionHandler(IaException.class) public ResponseEntity<ErroSaida> ia(IaException e,HttpServletRequest r) { return resposta(e.getStatus(),"falha_ia",e.getMessage(),r,Map.of()); }
    @ExceptionHandler({PersistenciaException.class,TransactionException.class}) public ResponseEntity<ErroSaida> banco(Exception e,HttpServletRequest r) {
        int status=e instanceof PersistenciaException p && !p.isIndisponivel()?500:503;
        log.warn("Falha de persistência tratada; status={}",status);
        return resposta(status,"falha_persistencia",status==503?"Banco indisponível. Confira a conexão e tente novamente.":"Não foi possível concluir a operação.",r,Map.of());
    }
    @ExceptionHandler(Exception.class) public ResponseEntity<ErroSaida> inesperado(Exception e,HttpServletRequest r) {
        log.error("Falha inesperada tratada: {}",e.getClass().getSimpleName());
        return resposta(500,"erro_interno","Não foi possível concluir a operação.",r,Map.of());
    }
}
