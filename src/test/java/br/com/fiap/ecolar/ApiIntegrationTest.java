package br.com.fiap.ecolar;

import com.fasterxml.jackson.databind.*;
import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

/** JDBC real em H2 modo Oracle. Nesta classe, a IA é um dublê HTTP explicitamente identificado. */
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired MockMvc mvc; @Autowired ObjectMapper mapper; @Autowired JdbcTemplate jdbc;
    static HttpServer stub; static AtomicInteger modo=new AtomicInteger(), chamadas=new AtomicInteger();
    static volatile JsonNode pedido;
    static {
        try {
            stub=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
            stub.createContext("/", ex -> {
                chamadas.incrementAndGet(); ObjectMapper m=new ObjectMapper(); pedido=m.readTree(ex.getRequestBody());
                String ref=pedido.get("historico").get(6).get("data").asText(); LocalDate d=LocalDate.parse(ref);
                Map<String,Object> r=new LinkedHashMap<>();r.put("modelo","teste-http");r.put("origem",modo.get()==4?"SIMULADO":"MODELO");r.put("avisos",List.of("Dublê HTTP, não modelo treinado."));
                if(ex.getRequestURI().getPath().equals("/prever")) {
                    r.put("data_referencia",ref);r.put("data_alvo",modo.get()==2?ref:d.plusDays(1).toString());r.put("consumo_previsto_kwh",modo.get()==1?-1:42.123);
                } else {r.put("data_inicio",d.minusDays(6).toString());r.put("data_fim",ref);r.put("grupo",0);r.put("nome","Grupo teste");r.put("interpretacao","Dublê controlado.");}
                byte[] bytes=m.writeValueAsBytes(r);ex.getResponseHeaders().set("Content-Type","application/json");ex.sendResponseHeaders(modo.get()==3?503:200,bytes.length);ex.getResponseBody().write(bytes);ex.close();
            });stub.start();
        } catch(Exception e) {throw new ExceptionInInitializerError(e);}
    }
    @DynamicPropertySource static void props(DynamicPropertyRegistry r) {r.add("ecolar.ia-url",() -> "http://127.0.0.1:"+stub.getAddress().getPort());}
    @AfterAll static void fechar() {stub.stop(0);}
    @BeforeEach void limpar() {
        for(String t:List.of("tb_analise_padrao","tb_previsao_consumo","tb_residencia_aparelho","tb_meta_consumo","tb_consumo_diario","tb_aparelho","tb_residencia")) jdbc.update("DELETE FROM "+t);
        modo.set(0);chamadas.set(0);
    }
    long id(MvcResult r,String campo) throws Exception {return mapper.readTree(r.getResponse().getContentAsString()).get(campo).asLong();}
    long casa() throws Exception {return id(mvc.perform(post("/residencias").contentType(MediaType.APPLICATION_JSON).content("{\"nmResidencia\":\"Casa teste\",\"qtMoradores\":3,\"vlTarifaKwh\":1}"))
        .andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn(),"cdResidencia");}
    MvcResult consumo(long c,String dia,String valor) throws Exception {return mvc.perform(post("/residencias/{id}/consumos",c).contentType(MediaType.APPLICATION_JSON).content("{\"dtConsumo\":\""+dia+"\",\"vlConsumoKwh\":"+valor+",\"dsOrigem\":\"SIMULADO\"}")).andReturn();}
    MvcResult meta(long c,String ini,String fim) throws Exception {return mvc.perform(post("/residencias/{id}/metas",c).contentType(MediaType.APPLICATION_JSON).content("{\"dtInicio\":\""+ini+"\",\"dtFim\":\""+fim+"\",\"vlLimiteKwh\":100}")).andReturn();}
    void sete(long c) throws Exception {for(int i=0;i<7;i++) assertThat(consumo(c,LocalDate.of(2020,1,1).plusDays(i).toString(),"8.5").getResponse().getStatus()).isEqualTo(201);}
    @Test void crudResidencia() throws Exception {
        long c=casa();mvc.perform(get("/residencias/{id}",c)).andExpect(status().isOk()).andExpect(jsonPath("$.qtMoradores").value(3));
        mvc.perform(get("/residencias")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/residencias/{id}",c).contentType(MediaType.APPLICATION_JSON).content("{\"nmResidencia\":\"Atualizada\",\"qtMoradores\":2,\"vlTarifaKwh\":0.9}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.nmResidencia").value("Atualizada"));
        mvc.perform(delete("/residencias/{id}",c)).andExpect(status().isNoContent()).andExpect(content().string(""));mvc.perform(get("/residencias/{id}",c)).andExpect(status().isNotFound());
    }
    @Test void crudConsumo() throws Exception {
        long c=casa();var r=consumo(c,"2020-01-01","6.2");assertThat(r.getResponse().getStatus()).isEqualTo(201);long n=id(r,"cdConsumo");
        mvc.perform(get("/consumos/{id}",n)).andExpect(status().isOk());mvc.perform(get("/consumos")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/consumos/{id}",n).contentType(MediaType.APPLICATION_JSON).content("{\"cdResidencia\":"+c+",\"dtConsumo\":\"2020-01-01\",\"vlConsumoKwh\":7.1,\"dsOrigem\":\"MANUAL\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.vlConsumoKwh").value(7.1));
        mvc.perform(delete("/consumos/{id}",n)).andExpect(status().isNoContent());mvc.perform(get("/consumos/{id}",n)).andExpect(status().isNotFound());
    }
    @Test void crudMeta() throws Exception {
        long c=casa();var r=meta(c,"2020-01-01","2020-01-31");assertThat(r.getResponse().getStatus()).isEqualTo(201);long n=id(r,"cdMeta");
        mvc.perform(get("/metas/{id}",n)).andExpect(status().isOk());mvc.perform(get("/metas")).andExpect(status().isOk());
        mvc.perform(put("/metas/{id}",n).contentType(MediaType.APPLICATION_JSON).content("{\"cdResidencia\":"+c+",\"dtInicio\":\"2020-01-01\",\"dtFim\":\"2020-01-31\",\"vlLimiteKwh\":120}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.vlLimiteKwh").value(120));
        mvc.perform(delete("/metas/{id}",n)).andExpect(status().isNoContent());mvc.perform(get("/metas/{id}",n)).andExpect(status().isNotFound());
    }
    @Test void duplicataConsumo() throws Exception {long c=casa();assertThat(consumo(c,"2020-01-01","1").getResponse().getStatus()).isEqualTo(201);assertThat(consumo(c,"2020-01-01","2").getResponse().getStatus()).isEqualTo(409);}
    @Test void metasSobrepostasInclusiveFronteira() throws Exception {long c=casa();assertThat(meta(c,"2020-01-01","2020-01-31").getResponse().getStatus()).isEqualTo(201);assertThat(meta(c,"2020-01-31","2020-02-15").getResponse().getStatus()).isEqualTo(409);assertThat(meta(c,"2020-02-01","2020-02-29").getResponse().getStatus()).isEqualTo(201);}
    @Test void concorrenciaMetas() throws Exception {
        long c=casa();ExecutorService pool=Executors.newFixedThreadPool(2);
        try {var a=pool.submit(() -> meta(c,"2020-01-01","2020-01-31").getResponse().getStatus());var b=pool.submit(() -> meta(c,"2020-01-01","2020-01-31").getResponse().getStatus());assertThat(List.of(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS))).containsExactlyInAnyOrder(201,409);} finally {pool.shutdownNow();}
    }
    @Test void periodoInvertido() throws Exception {assertThat(meta(casa(),"2020-01-31","2020-01-01").getResponse().getStatus()).isEqualTo(400);}
    @Test void paiComFilhos() throws Exception {long c=casa();consumo(c,"2020-01-01","1");mvc.perform(delete("/residencias/{id}",c)).andExpect(status().isConflict());}
    @ParameterizedTest @ValueSource(strings={"consumos","metas"}) void paiAusente(String rota) throws Exception {mvc.perform(get("/residencias/999999/"+rota)).andExpect(status().isNotFound());}
    @ParameterizedTest @ValueSource(strings={"residencias","consumos","metas","previsoes","analises"}) void idAusente(String rota) throws Exception {mvc.perform(get("/"+rota+"/999999")).andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON));}
    @ParameterizedTest @ValueSource(strings={"{}","{\"nmResidencia\":\"\",\"qtMoradores\":0,\"vlTarifaKwh\":-1}","{\"nmResidencia\":\"Casa\",\"qtMoradores\":1.5,\"vlTarifaKwh\":1}","{\"nmResidencia\":\"Casa\",\"qtMoradores\":2,\"vlTarifaKwh\":1,\"senha\":\"x\"}"})
    void validacoes(String json) throws Exception {mvc.perform(post("/residencias").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());}
    @Test void consumoFuturoENegativo() throws Exception {long c=casa();assertThat(consumo(c,LocalDate.now().plusDays(3).toString(),"1").getResponse().getStatus()).isEqualTo(400);assertThat(consumo(c,"2020-01-01","-1").getResponse().getStatus()).isEqualTo(400);}
    @Test void isolamento() throws Exception {long a=casa(),b=casa();consumo(a,"2020-01-01","10");mvc.perform(get("/residencias/{id}/consumos",b)).andExpect(jsonPath("$.length()").value(0));}
    @Test void naoTransferirFilho() throws Exception {long a=casa(),b=casa(),n=id(consumo(a,"2020-01-01","1"),"cdConsumo");mvc.perform(put("/consumos/{id}",n).contentType(MediaType.APPLICATION_JSON).content("{\"cdResidencia\":"+b+",\"dtConsumo\":\"2020-01-01\",\"vlConsumoKwh\":1,\"dsOrigem\":\"MANUAL\"}")).andExpect(status().isConflict());}
    @Test void resumoSemZerosInventados() throws Exception {long c=casa();consumo(c,"2020-01-01","10");consumo(c,"2020-01-03","20");mvc.perform(get("/residencias/{id}/resumo",c).param("inicio","2020-01-01").param("fim","2020-01-03")).andExpect(status().isOk()).andExpect(jsonPath("$.totalKwh").value(30)).andExpect(jsonPath("$.mediaDiariaKwh").value(15)).andExpect(jsonPath("$.coberturaPercentual").value(66.67));}
    @Test void metaSemDadosEParcial() throws Exception {long c=casa(),n=id(meta(c,"2020-01-01","2020-01-03"),"cdMeta");mvc.perform(get("/metas/{id}/acompanhamento",n)).andExpect(jsonPath("$.situacao").value("SEM DADOS"));consumo(c,"2020-01-01","10");mvc.perform(get("/metas/{id}/acompanhamento",n)).andExpect(jsonPath("$.situacao").value("PARCIAL"));}
    @Test void metodosEFormatos() throws Exception {
        mvc.perform(patch("/residencias/1")).andExpect(status().isMethodNotAllowed()).andExpect(header().exists("Allow"));mvc.perform(post("/residencias").contentType(MediaType.TEXT_PLAIN).content("x")).andExpect(status().isUnsupportedMediaType());mvc.perform(get("/nao-existe")).andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON));mvc.perform(get("/residencias/-1")).andExpect(status().isBadRequest());mvc.perform(get("/residencias/abc")).andExpect(status().isBadRequest());
    }
    @Test void textoNaoViraSql() throws Exception {String nome="Casa ' OR 1=1 --";mvc.perform(post("/residencias").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(Map.of("nmResidencia",nome,"qtMoradores",1,"vlTarifaKwh",1)))).andExpect(status().isCreated()).andExpect(jsonPath("$.nmResidencia").value(nome));assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_residencia",Integer.class)).isEqualTo(1);}
    @Test void cors() throws Exception {mvc.perform(options("/residencias").header("Origin","http://localhost:5173").header("Access-Control-Request-Method","POST")).andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:5173"));}
    @Test void locationRespeitaHttpsDoProxy() throws Exception {
        mvc.perform(post("/residencias").header("X-Forwarded-Proto","https").header("X-Forwarded-Host","ecolar-teste.example")
            .contentType(MediaType.APPLICATION_JSON).content("{\"nmResidencia\":\"Proxy\",\"qtMoradores\":1,\"vlTarifaKwh\":1}"))
            .andExpect(status().isCreated()).andExpect(header().string("Location",org.hamcrest.Matchers.startsWith("https://ecolar-teste.example/residencias/")));
    }
    @Test void health() throws Exception {mvc.perform(get("/health")).andExpect(status().isOk()).andExpect(jsonPath("$.banco").value("conectado"));}
    @Test void historicoInsuficiente() throws Exception {long c=casa();mvc.perform(post("/residencias/{id}/previsoes",c).contentType(MediaType.APPLICATION_JSON).content("{\"dataReferencia\":\"2020-01-07\"}")).andExpect(status().isConflict());assertThat(chamadas.get()).isZero();}
    @Test void fluxoIaComPersistencia() throws Exception {
        long c=casa();sete(c);var r=mvc.perform(post("/residencias/{id}/previsoes",c).contentType(MediaType.APPLICATION_JSON).content("{\"dataReferencia\":\"2020-01-07\"}"))
            .andExpect(status().isCreated()).andExpect(header().exists("Location")).andExpect(jsonPath("$.vlPrevistoKwh").value(42.123)).andExpect(jsonPath("$.dsOrigem").value("MODELO")).andReturn();
        assertThat(pedido.get("historico").size()).isEqualTo(7);assertThat(pedido.get("historico").get(0).get("consumo_kwh").decimalValue()).isEqualByComparingTo("8.5");
        mvc.perform(get(URI.create(r.getResponse().getHeader("Location")))).andExpect(status().isOk());mvc.perform(post("/residencias/{id}/analises",c).contentType(MediaType.APPLICATION_JSON).content("{\"dataReferencia\":\"2020-01-07\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.cdGrupo").value(0));assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_previsao_consumo",Integer.class)).isEqualTo(1);assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_analise_padrao",Integer.class)).isEqualTo(1);
    }
    @ParameterizedTest @ValueSource(ints={1,2,4}) void saidaIaInvalidaNaoGrava(int valor) throws Exception {long c=casa();sete(c);modo.set(valor);mvc.perform(post("/residencias/{id}/previsoes",c).contentType(MediaType.APPLICATION_JSON).content("{\"dataReferencia\":\"2020-01-07\"}")).andExpect(status().isBadGateway());assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_previsao_consumo",Integer.class)).isZero();}
    @Test void iaIndisponivelNaoGrava() throws Exception {long c=casa();sete(c);modo.set(3);mvc.perform(post("/residencias/{id}/previsoes",c).contentType(MediaType.APPLICATION_JSON).content("{\"dataReferencia\":\"2020-01-07\"}")).andExpect(status().isServiceUnavailable());assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tb_previsao_consumo",Integer.class)).isZero();}
}
