# Publicação Java no Render

**Estado deste pacote:** código e configuração preparados; publicação remota ainda não realizada.
Não existe URL pública de API ou vídeo preenchida ficticiamente.

## Preparar e publicar

1. Subir o projeto Java em um repositório escolhido pelo grupo, sem senhas, .env, target,
   arquivos privados de configuração ou cache. Não é necessário publicar bin/ecolar-api.jar:
   o Docker recompila a partir do código-fonte.
2. No Render, criar um Web Service e conectar esse repositório; escolher Docker.
3. Se o projeto estiver dentro de subpasta, configurar Root Directory como EcoLar_Java.
   Dockerfile Path: ./Dockerfile; contexto: raiz do projeto Java.
4. Informar as variáveis abaixo no painel, nunca no código. O Render fornece PORT.
5. Health Check Path: /health (verifica conexão com banco). A aplicação deve receber conexão
   Oracle real. Se a rede/porta da FIAP bloquear o Render, investigar com a disciplina;
   não substituir por H2 para apresentar persistência Oracle como concluída.
6. Conferir o log de build, /health e os CRUDs pela URL remota. Testar CORS do front publicado.
7. Copiar a URL real para README.md e regenerar o PDF com o script de documentação.

render.yaml é uma alternativa de configuração por Blueprint. Ele referencia o plano free;
confira a disponibilidade e as condições apresentadas na sua conta antes de criar o serviço.
Não há autorização neste pacote para contratar plano pago automaticamente.

## Variáveis de ambiente (sem valores secretos)

| Variável | Finalidade |
|---|---|
| ORACLE_URL | URL JDBC com host, porta e serviço/SID, sem usuário/senha embutidos |
| ORACLE_USER | Usuário do schema do grupo |
| ORACLE_PASSWORD | Senha do schema; configurar como segredo no painel |
| CORS_ORIGINS | Origens exatas do front, separadas por vírgulas; incluir Vercel e localhost se necessários |
| IA_BASE_URL | URL HTTP(S) acessível do serviço Flask |
| PORT | Porta definida pelo Render; local padrão 8080 |

## Docker

Multi-stage: maven:3.9.11-eclipse-temurin-21 compila; eclipse-temurin:21-jre executa
como usuário sem privilégios. O JAR é copiado do estágio de build; não depende de target
no ZIP. O banco não é iniciado no contêiner nem criado pelo Spring.

Teste opcional local, com Docker instalado e variáveis definidas no ambiente:

```bash
docker build -t ecolar-api .
docker run --rm -p 8080:8080 -e ORACLE_URL -e ORACLE_USER -e ORACLE_PASSWORD -e IA_BASE_URL -e CORS_ORIGINS ecolar-api
```

Dentro de um contêiner ou no Render, 127.0.0.1 aponta para o próprio contêiner,
não para seu computador. O Flask local não poderá ser acessado pelo Render nessa URL.
Se as funções de IA forem demonstradas remotamente, publicar o Flask em um serviço
separado e informar a URL em IA_BASE_URL. CRUD Java não depende de a IA estar online.

## Publicar a IA (se necessário para demonstração remota integrada)

A entrega aprovada EcoLar_IA já contém app.py e os modelos. Preparar seu Dockerfile separado
com Python 3.12, instalar requirements.txt e um servidor WSGI, como Gunicorn. Não usar
Flask em debug. O comando deve carregar app:create_app(), ouvir 0.0.0.0 na PORT do provedor
e manter os modelos disponíveis. Essa publicação não foi executada nesta revisão Java.

## Fontes oficiais consultadas na preparação

https://render.com/docs/docker
https://render.com/docs/health-checks
https://render.com/docs/deploys
https://docs.spring.io/spring-boot/3.5/system-requirements.html
