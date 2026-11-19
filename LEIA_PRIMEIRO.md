# EcoLar Java — abrir a pasta EcoLar_Java no VS Code

## O que este pacote contém

API Spring Boot/JDBC com CRUD de residências, consumos e metas; DTOs e ModelMapper;
validações, erros JSON, regras de negócio, integração com Flask e persistência dos resultados;
Dockerfile multi-stage, configuração Render, SQL, coleção Postman e PDF técnico.
O JAR compilado fica em bin/ecolar-api.jar para começar sem instalar Maven.

## Antes de começar

Instale **JDK 21 de 64 bits**, não Python. Confira `java -version` e, para compilar,
`javac -version`. Fonte oficial: https://adoptium.net/temurin/releases/?version=21&os=windows&arch=x64.
No VS Code, a extensão Extension Pack for Java é recomendada.
Este backend usa o Oracle existente do grupo. Não precisa recriar o banco.
Não coloque credenciais em chats, GitHub, ZIP de entrega ou Postman.

## Primeiro teste no Windows

1. Extraia o ZIP inteiro e abra **EcoLar_Java** no VS Code.
2. Confira se sua conexão funciona no SQL Developer. O SQL Developer pode ser fechado depois.
3. No terminal PowerShell, execute `./INICIAR_JAVA_WINDOWS.bat`.
4. Informe a URL JDBC, usuário e senha quando o script pedir. A senha não aparece nem é salva
   em arquivo; só é passada ao processo Java. Os dados são os da conexão Oracle do grupo.
5. Para o Flask, pressione Enter se ele estiver em http://127.0.0.1:5000 no mesmo computador.
6. Abra **http://localhost:8080** no navegador e clique em **Verificar banco** e **Listar residências**.
7. Faça cadastro com nome como Casa de teste Java e guarde o ID retornado para continuar os testes.
8. Envie prints dos resultados ou erros. Não envie a senha nem um print de configuração com a senha visível.

### Montar a URL JDBC

Se o SQL Developer usa **nome do serviço**:
`jdbc:oracle:thin:@//HOST:PORTA/SERVICO`

Se usa **SID**:
`jdbc:oracle:thin:@HOST:PORTA:SID`

Copie host, porta e serviço/SID das propriedades da conexão. O apelido FIAP_Rm... não é
necessariamente o host ou o serviço. Usuário é informado separadamente da URL.
Não substitua os campos pelo nome do computador local.

A primeira execução de CRUD usa as tabelas e sequences já criadas. Java não cria tabelas,
não apaga fixtures e não executa os scripts SQL automaticamente.

## Recompilar ou rodar testes

O Maven Wrapper incluído baixa Maven na primeira utilização. Precisa de internet no início:

```powershell
.\mvnw.cmd test
.\COMPILAR_WINDOWS.bat
```

O segundo comando recompila, testa e atualiza o JAR de bin. Não altere o código e continue
usando um JAR antigo. No Linux/macOS: `./mvnw verify` e copiar o JAR de target para bin.
A pasta target é gerada pelo Maven e não deve entrar no ZIP da entrega.

Os testes padrão usam H2 **somente no escopo de teste**, em modo Oracle; isso não prova
conexão com o Oracle da FIAP. O aplicativo normal usa o driver Oracle, sem fallback para H2.
Resultado e limites da revisão estão em docs/VALIDACAO.md.

## Testar a IA integrada

1. Abra a pasta da entrega **EcoLar_IA** em outro terminal e inicie o Flask como já testado.
2. Inicie o Java com IA_BASE_URL apontando para esse Flask.
3. Na casa 1 da carga original, já há sete dias consecutivos até 14/10/2026.
4. POST `/residencias/1/previsoes` com `{"dataReferencia":"2026-10-14"}`.
5. POST `/residencias/1/analises` com o mesmo body.
6. As respostas 201 recebem IDs novos e origem MODELO. Os consumos usados são SIMULADO;
   o resultado continua sendo inferência real, com aviso sobre a entrada de demonstração.
7. Confira o histórico nas rotas GET e, se quiser, no SQL Developer. Não altere fixtures para MODELO.

Se usar outra residência/data, selecione sete dias sem lacunas. Casa Jardim tem apenas seis dias
na carga original: precisa completar histórico antes da IA. Não invente zero para um dia sem medição.

## Deploy, front e vídeo: ainda requerem ação do grupo

O código e os arquivos de deploy estão preparados. **Não há URL de Render ou de vídeo inventada.**
A publicação precisa da conta/repositório escolhido pelo grupo, das variáveis Oracle e de conectividade
com o banco. O React do Vinícius ainda precisa ser fornecido/conectado para comprovar a integração.
Depois do deploy e da gravação, inserir os links reais no README e PDF técnico antes do Portal.
Detalhes em docs/DEPLOY.md e docs/FINALIZACAO.md. Não considere o requisito de deploy concluído
apenas por existir um Dockerfile.

O console local não é o front React final. O vídeo de Java deve ter até cinco minutos, YouTube não listado,
mostrando o front do grupo integrado. A apresentação presencial exige demonstração da API publicada.

## Se o banco ainda não existir em um schema separado

Somente em um **schema vazio**, usar sql/01_estrutura_oracle.sql no SQL Developer (F5).
Opcionalmente, sql/02_carga_demonstracao_opcional.sql insere a carga original de 60 registros.
**Se o banco do Yan já está preenchido, não execute esses scripts de novo.**

## Problemas comuns

- Java incompatível: instalar JDK 21, reabrir o VS Code e verificar JAVA_HOME.
- Falha JDBC: conferir host, serviço/SID, porta e credenciais, sem publicá-los; testar no SQL Developer.
- HTTP 500 ao listar com conexão válida: conferir se as sete tabelas e sequences estão no mesmo schema.
- HTTP 503 de IA: Flask não iniciado, URL errada ou modelos indisponíveis.
- HTTP 409 de IA: histórico insuficiente; 400: body/data inválidos.
- Porta 8080 em uso: encerrar a outra instância; PORT pode ser configurada no ambiente.
- Maven falha no download: conferir internet e JDK, sem desabilitar a verificação TLS.
- Render não acessa Oracle: investigar rede/porta/restrições da FIAP. Não substituir por banco em memória
  para dizer que houve integração Oracle.

## Identificação

Yan Almeida RM568814; Vinicius Pacheco Ruiz RM571109; Diego Barbosa RM568829.
Turma 1TDSPV. Confirmar dados oficiais e revisar o conteúdo antes do envio.
