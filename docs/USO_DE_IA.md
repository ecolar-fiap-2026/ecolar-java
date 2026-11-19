# Uso de IA e revisão

Projeto preparado em 16/11/2026 com ChatGPT/Codex, para a solução EcoLar do próprio grupo.
Pedido: assumir a frente Java porque Diego não conseguirá realizá-la e entregar pasta ZIP
pronta para abrir e testar, como foi feito com a IA.

Apoio: leitura da rubrica, revisão do SQL/contrato Flask, implementação específica do domínio,
testes, organização e documentação. O projeto não veio de outro grupo ou catálogo de APIs.
Não foram inventadas credenciais, URLs de deploy, vídeo, testes na conta Oracle ou participação
humana ainda não relatada nesta etapa.

Decisões: três entidades de CRUD (residência, consumo, meta), JDBC com PreparedStatement,
ModelMapper, regras de sobreposição/duplicidade e integração com os modelos existentes.
A camada de histórico de inferência é somente inclusão/consulta, para preservar registros.
Não foi alterado o schema já aprovado, nem foram renomeados fixtures para MODELO.

O grupo deve executar no computador, revisar o código, registrar correções humanas reais
e compreender DTO → controller → service → DAO → Oracle. Teste automatizado não equivale
a domínio técnico de todos os integrantes. A publicação e o vídeo continuam pendentes até
ocorrerem e terem seus links registrados.
