# Revisão e testes — 16/11/2026

## Executado no ambiente de preparação

- Build Maven verify com JDK 21, compilação e empacotamento Spring Boot concluídos.
- 35 testes principais aprovados: CRUD das três entidades, DTOs/ModelMapper, validação,
  400/404/405/409/415, CORS, Location HTTPS atrás de proxy, texto vinculado por PreparedStatement,
  isolamento de casas, sobreposição e concorrência de metas, cobertura/média, integração JDBC
  e preservação da persistência quando a IA falha ou devolve dados inválidos.
- Banco dos testes: H2 em modo Oracle, com script de teste derivado do SQL original.
  COMMENT e unidade CHAR foram adaptados apenas no script de teste; o DDL Oracle entregue
  permanece o do grupo. Não foi usado JPA, Hibernate ou Spring Data.
- Teste adicional com Tomcat HTTP real e Flask real, carregando os modelos exportados
  da entrega de IA: consulta de sete dias via JDBC, previsão e agrupamento, validação,
  inclusão e leitura do histórico. Persistência desse experimento também em H2, não FIAP.
- JAR compilado inclui driver Oracle e não inclui H2 nem Hibernate ORM (hibernate-core). O app normal usa
  Oracle e não possui fallback automático para banco em memória.
- Console JavaScript verificado sintaticamente; página servida e conferida no teste HTTP.
- SQL confrontado com o schema recebido: sete tabelas, sete sequences e 60 inserts opcionais.
- PDF técnico renderizado e revisado; integridade do ZIP e exclusão de target/credenciais conferidas.

Logs: build_verify.log, TESTES_CRUD_JDBC.txt e TESTE_FLASK_REAL.txt.
O teste com Flask real é condicional: ele não é executado no mvnw test padrão, porque
precisa do serviço Flask. Para reproduzir com Flask aberto na porta 5000:

```powershell
.\mvnw.cmd -Dtest=IaRealIntegrationTest -Decolar.realIa=true -Decolar.iaBase=http://127.0.0.1:5000 test
```

## Ainda não comprovado nesta preparação

- Conexão e CRUD no Oracle da FIAP: depende dos dados de conexão configurados localmente pelo grupo.
- Execução no Windows do aluno: scripts e instruções preparados, a conferir juntos.
- Build Docker: o Dockerfile está preparado, mas não havia daemon Docker disponível neste ambiente.
- Deploy real no Render e acesso Oracle a partir dele.
- Integração com o front React do grupo e gravação/publicação do vídeo.

Esses itens não são declarados concluídos só por haver configuração ou testes com H2.
O console de diagnóstico não substitui o React exigido pelo enunciado. Os links reais de API
no Render e vídeo precisam ser preenchidos no README e PDF antes do envio final.
