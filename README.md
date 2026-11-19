# EcoLar Java — Global Solution 2026/2

**Disciplina:** Domain Driven Design Using Java. **Turma:** 1TDSPV.

- Yan Almeida — RM568814
- Vinicius Pacheco Ruiz — RM571109
- Diego Barbosa — RM568829

## Links de entrega

- API no Render: **PENDENTE DE PUBLICAÇÃO — inserir URL real.**
- Vídeo YouTube não listado (até 5 min): **PENDENTE DE GRAVAÇÃO — inserir URL real.**
- Front React integrado: **PENDENTE DE VALIDAÇÃO com o projeto do grupo.**

Não enviar como entrega encerrada enquanto os links obrigatórios estiverem pendentes.
Código, documentação e configuração estão preparados para os testes locais e publicação.

## Como rodar

Leia **LEIA_PRIMEIRO.md**. Instale JDK 21, abra esta pasta no VS Code e execute
INICIAR_JAVA_WINDOWS.bat. Informe os dados da conexão Oracle quando solicitado.
Abra http://localhost:8080. Não executar scripts SQL em um banco já criado.

Para compilar/testar: `mvnw.cmd verify` (Windows) ou `./mvnw verify` (Linux/macOS).
COMPILAR_WINDOWS.bat atualiza o JAR de bin após os testes. Dependências na primeira
compilação requerem internet. JPA/Hibernate/Spring Data não são usados.

## Arquivos

- src/main/java: model, dao, dto, service, controller, exception, handler, config.
- src/main/resources/static: console de diagnóstico que usa Fetch real.
- sql: estrutura Oracle de sete tabelas/sequences e carga sintética opcional.
- docs/EcoLar_Java_Documentacao.pdf: documentação técnica.
- postman: coleção de CRUD e erros.
- Dockerfile e render.yaml: publicação a partir do código-fonte.
- bin/ecolar-api.jar: compilado para teste local; não é a pasta target.
- docs/VALIDACAO.md: testes feitos e limites.

Integração com IA: Java consulta sete dias no Oracle, chama Flask e persiste inferências
validadas. Não há conexão automática com o JSON da disciplina de Python.

Para finalizar: docs/FINALIZACAO.md. Uso de IA declarado em docs/USO_DE_IA.md.
