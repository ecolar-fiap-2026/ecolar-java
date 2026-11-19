# Contrato EcoLar Java — versão 1.0.0

Base local: http://localhost:8080. Recursos JSON; IDs novos vêm das sequences Oracle.
Campos Java/JSON usam camelCase; JDBC faz a correspondência explícita com snake_case do Oracle.
Não há prefixo /api. Todos os bodies POST/PUT usam Content-Type: application/json.

## CRUD completo das três entidades do domínio

| Método | Rota | Sucesso | Erros comuns |
|---|---|---|---|
| POST | /residencias | 201 + Location | 400 |
| GET | /residencias | 200, array | 503 |
| GET | /residencias/{id} | 200 | 400, 404 |
| PUT | /residencias/{id} | 200 | 400, 404 |
| DELETE | /residencias/{id} | 204, sem corpo | 404, 409 |
| POST | /consumos | 201 + Location | 400, 404, 409 |
| GET | /consumos | 200, array | 503 |
| GET | /consumos/{id} | 200 | 400, 404 |
| PUT | /consumos/{id} | 200 | 400, 404, 409 |
| DELETE | /consumos/{id} | 204 | 404 |
| POST | /metas | 201 + Location | 400, 404, 409 |
| GET | /metas | 200, array | 503 |
| GET | /metas/{id} | 200 | 400, 404 |
| PUT | /metas/{id} | 200 | 400, 404, 409 |
| DELETE | /metas/{id} | 204 | 404 |

## Relacionamentos, cálculos e inferências

| Método | Rota | Sucesso |
|---|---|---|
| GET | /residencias/{id}/consumos | 200, array; pai ausente 404 |
| POST | /residencias/{id}/consumos | 201 + Location; ID da casa vem da rota |
| GET | /residencias/{id}/metas | 200, array; pai ausente 404 |
| POST | /residencias/{id}/metas | 201 + Location; ID da casa vem da rota |
| GET | /residencias/{id}/resumo?inicio=2020-01-01&fim=2020-01-31 | 200 |
| GET | /metas/{id}/acompanhamento | 200 |
| POST | /residencias/{id}/previsoes | 201 + Location |
| GET | /residencias/{id}/previsoes | 200, array |
| GET | /previsoes/{id} | 200 |
| POST | /residencias/{id}/analises | 201 + Location |
| GET | /residencias/{id}/analises | 200, array |
| GET | /analises/{id} | 200 |
| GET | /health/live | 200, aplicação em execução |
| GET | /health | 200 com conexão JDBC; 503 se banco indisponível |

O histórico de inferências é somente inclusão/consulta: não existe CRUD de edição ou exclusão
para previsões e análises. São registros de execução, não as três entidades de CRUD avaliadas.
Catálogo de aparelhos/vínculos é preservado no banco do grupo e está fora deste CRUD web.

## Exemplos de bodies de entrada

Residência (POST e PUT):
```json
{"nmResidencia":"Casa de teste","qtMoradores":3,"vlTarifaKwh":1.0}
```
Consumo aninhado:
```json
{"dtConsumo":"2020-01-01","vlConsumoKwh":6.2,"dsOrigem":"MANUAL"}
```
Meta aninhada:
```json
{"dtInicio":"2020-01-01","dtFim":"2020-01-31","vlLimiteKwh":180}
```
No POST /consumos, POST /metas e PUT de filhos, acrescentar cdResidencia numérico.
Nas rotas aninhadas esse campo não deve ser enviado: a residência vem da URL.
Dados desconhecidos e inteiros fracionários são rejeitados com 400.

Solicitação de IA (referência do último dia observado):
```json
{"dataReferencia":"2026-10-14"}
```
Exige sete consumos consecutivos da residência; retorna 409 se o histórico estiver incompleto.
Java envia {historico:[{data,consumo_kwh},...]} ao Flask. Ao receber saída válida,
usa as sequences existentes e persiste origem MODELO, sem alterar os fixtures SIMULADO.
Consumos simulados como entrada geram aviso explícito. Erro do Flask nunca produz resultado fictício.

## Exemplos de saída

Residência criada (ID 1000 é ilustrativo; o valor real vem da sequence):
```json
{"cdResidencia":1000,"nmResidencia":"Casa de teste","qtMoradores":3,"vlTarifaKwh":1.0}
```
Consumo:
```json
{"cdConsumo":1000,"cdResidencia":1000,"dtConsumo":"2020-01-01","vlConsumoKwh":6.2,"dsOrigem":"MANUAL"}
```
Meta:
```json
{"cdMeta":1000,"cdResidencia":1000,"dtInicio":"2020-01-01","dtFim":"2020-01-31","vlLimiteKwh":180}
```
Erro padronizado (timestamp e ID variam):
```json
{"timestamp":"2026-11-16T18:00:00-03:00","status":404,"codigo":"nao_encontrado","mensagem":"Residência não encontrada.","caminho":"/residencias/999999","campos":{}}
```
Nenhuma resposta de erro expõe SQL, senha ou stack trace. 405 inclui Allow.
415 para conteúdo não JSON; 406 para Accept incompatível; 500 para falha inesperada genérica.
503 para indisponibilidade de banco/IA; 502 para saída inválida da IA; 504 para timeout de IA.

## Regras e limites

- Um consumo por casa e dia, sem data futura; 0 a 10000 kWh/dia, até três casas decimais.
- Nome da residência não vazio, máximo 100 caracteres; moradores 1–9999.
- Tarifa não negativa: seis dígitos inteiros e quatro decimais; não é tarifa oficial.
- Meta positiva, até nove dígitos inteiros e três decimais; fim maior/igual ao início.
- Metas da mesma casa não podem se sobrepor (ambos os limites inclusivos).
- Não transferir consumo/meta para outra casa por PUT; não remover casa com dependências.
- Bloqueio da residência em transações serializa escritas concorrentes na mesma casa.
- Resumo: até 3660 dias, sem inventar consumo zero para dias ausentes. Média usa dias registrados;
  custo usa tarifa atual e não inclui impostos/bandeiras/custos fixos.
- O esquema Oracle não guarda a lista completa de avisos da inferência. POST retorna avisos originais;
  consultas do histórico informam essa limitação. Interpretação do grupo é persistida até 500 caracteres.

## Front-end

Coleção em postman/EcoLar_Java.postman_collection.json: executar na ordem para criar e
remover apenas os próprios registros de teste. O console em / usa Fetch real, mas é uma
ferramenta de teste; não substitui o React/Vite/TypeScript/Tailwind exigido no front do grupo.
Para testar CORS fora da API, autorizar a origem exata em CORS_ORIGINS, separada por vírgulas.
