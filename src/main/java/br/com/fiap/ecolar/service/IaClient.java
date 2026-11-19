package br.com.fiap.ecolar.service;
import br.com.fiap.ecolar.dto.IaRequisicao;
import br.com.fiap.ecolar.exception.IaException;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IaClient {
    private final HttpClient client; private final ObjectMapper mapper; private final String base;
    public IaClient(HttpClient client,ObjectMapper mapper,@Value("${ecolar.ia-url}") String base) {
        this.client=client; this.mapper=mapper; this.base=base.replaceAll("/+$", "");
        URI uri=URI.create(this.base);
        if (!java.util.Set.of("http","https").contains(uri.getScheme()) || uri.getHost()==null || uri.getUserInfo()!=null || uri.getQuery()!=null || uri.getFragment()!=null)
            throw new IllegalArgumentException("IA_BASE_URL deve ser uma URL HTTP(S) de serviço, sem credenciais ou consulta.");
    }
    public JsonNode chamar(String rota,IaRequisicao corpo) {
        if (!java.util.Set.of("prever","agrupar").contains(rota)) throw new IllegalArgumentException("Rota de IA inválida.");
        try {
            HttpRequest pedido=HttpRequest.newBuilder(URI.create(base+"/"+rota)).timeout(Duration.ofSeconds(12))
                .header("Content-Type","application/json").header("Accept","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(corpo))).build();
            HttpResponse<String> resposta=client.send(pedido,HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));
            if (resposta.statusCode()==503) throw new IaException(503,"Modelos da IA indisponíveis. Verifique o serviço Flask.");
            if (resposta.statusCode()!=200) throw new IaException(502,"O serviço de IA retornou uma resposta incompatível.");
            if (resposta.body().length()>65536) throw new IaException(502,"Resposta de IA acima do limite permitido.");
            JsonNode json=mapper.readTree(resposta.body());
            if (json==null || !json.isObject()) throw new IaException(502,"Formato de resposta da IA inválido.");
            return json;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) { throw new IaException(502,"Formato de resposta da IA inválido."); }
        catch (HttpTimeoutException e) { throw new IaException(504,"O serviço de IA não respondeu no prazo."); }
        catch (IOException e) { throw new IaException(503,"Não foi possível acessar a IA. Confira IA_BASE_URL e se o Flask está iniciado."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IaException(503,"Chamada da IA interrompida."); }
    }
}
