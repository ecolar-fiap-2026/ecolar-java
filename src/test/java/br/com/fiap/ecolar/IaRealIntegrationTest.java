package br.com.fiap.ecolar;

import com.fasterxml.jackson.databind.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import static org.assertj.core.api.Assertions.*;

/** Tomcat HTTP real → Flask real → modelos exportados; persistência local em H2, não Oracle FIAP. */
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnabledIfSystemProperty(named="ecolar.realIa", matches="true")
class IaRealIntegrationTest {
    @Autowired TestRestTemplate http; @Autowired ObjectMapper mapper; @Autowired JdbcTemplate jdbc;
    @DynamicPropertySource static void props(DynamicPropertyRegistry r) {
        r.add("ecolar.ia-url",() -> System.getProperty("ecolar.iaBase","http://127.0.0.1:5000"));
        r.add("spring.datasource.url",() -> "jdbc:h2:mem:ecolar_ia_real;MODE=Oracle;DB_CLOSE_DELAY=-1");
    }
    JsonNode post(String rota,Map<String,Object> corpo) throws Exception {
        var resposta=http.postForEntity(rota,corpo,String.class);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getHeaders().getLocation()).isNotNull();
        return mapper.readTree(resposta.getBody());
    }
    @Test void fluxoHttpRealDosDoisModelos() throws Exception {
        var pagina=http.getForEntity("/",String.class);
        assertThat(pagina.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pagina.getBody()).contains("Console de teste");
        long casa=post("/residencias",Map.of("nmResidencia","Teste UCI importado","qtMoradores",1,"vlTarifaKwh",1)).get("cdResidencia").asLong();
        double[] valores={21.645467,12.781767,15.597233,11.381333,18.895433,13.2452,22.874533};
        // Oracle do grupo aceita três casas decimais; os valores importados são arredondados na entrada.
        for(int i=0;i<7;i++) post("/residencias/"+casa+"/consumos",Map.of("dtConsumo","2010-03-0"+(i+1),
            "vlConsumoKwh",java.math.BigDecimal.valueOf(valores[i]).setScale(3,java.math.RoundingMode.HALF_UP),"dsOrigem","IMPORTADO"));
        var previsao=post("/residencias/"+casa+"/previsoes",Map.of("dataReferencia","2010-03-07"));
        assertThat(previsao.get("dsOrigem").asText()).isEqualTo("MODELO");
        assertThat(previsao.get("dsModelo").asText()).contains("random-forest");
        assertThat(previsao.get("dtAlvo").asText()).isEqualTo("2010-03-08");
        assertThat(previsao.get("vlPrevistoKwh").asDouble()).isGreaterThanOrEqualTo(0);
        var analise=post("/residencias/"+casa+"/analises",Map.of("dataReferencia","2010-03-07"));
        assertThat(analise.get("dsModelo").asText()).contains("kmeans");
        assertThat(analise.get("dsOrigem").asText()).isEqualTo("MODELO");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_previsao_consumo WHERE cd_residencia=?",Integer.class,casa)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_analise_padrao WHERE cd_residencia=?",Integer.class,casa)).isEqualTo(1);
        assertThat(http.getForEntity("/previsoes/"+previsao.get("cdPrevisao").asLong(),String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println("INTEGRACAO_REAL_FLASK: previsao="+previsao+"; analise="+analise);
    }
}
