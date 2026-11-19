# Antes da entrega de Java no Portal

O enunciado exige código, persistência Oracle, API publicada e integração com o front,
além de PDF e vídeo. Este ZIP é o projeto revisado para execução; as ações externas
abaixo precisam de evidências reais, não podem ser substituídas por documentação de intenção.

1. Executar no Windows com JDK 21 e Oracle da FIAP. Validar CRUD e erros no Postman.
2. Vinícius conectar o React ao contrato docs/API.md: lista e cadastro reais via Fetch.
3. Publicar a API no Render com Oracle acessível. Registrar URL e testar sem sessão do proprietário.
4. Gravar vídeo de até cinco minutos no YouTube não listado mostrando o front do grupo
   integrado e a explicação. Não confundir com o pitch de Business Model, que é outra entrega.
5. Atualizar README e PDF com URLs reais da API e do vídeo. O script de PDF aceita esses links.
6. Confirmar nomes oficiais, RMs, turma e entendimento de todos. Não há garantia de nota.
7. Montar ZIP único sem target, credenciais, cache, .git ou arquivos privados.

Até esses itens, o documento identifica deploy/vídeo/front como pendentes. O console HTML
incluído serve para diagnóstico e não substitui o front React da disciplina.

## Ordem sugerida para testar juntos

- Primeiro: java -version, iniciar o script e verificar /health.
- Depois: listar os três registros originais de residência, criar uma casa de teste,
  consumo e meta; testar 400, 404, 409 e remover somente os registros criados no teste.
- Depois: iniciar o Flask e gerar previsão/agrupamento, lendo os registros persistidos.
- Por último: front, deploy, vídeo e atualização dos links.

O banco já criado não deve ser recriado. O SQL fornecido é para schema vazio ou avaliação
pelo professor. Os testes automatizados não acessam a conta FIAP e não apagam dados dela.

## Atualizar o PDF depois da publicação

Na pasta EcoLar_Java, com Python instalado (não é necessário para rodar o Java):

```powershell
py -3.12 -m pip install -r scripts/requirements-documentacao.txt
py -3.12 scripts/gerar_documentacao.py --api-url https://SUA-API.onrender.com --video-url https://youtu.be/SEU-VIDEO
```

Substituir os exemplos pelas URLs reais. O script atualiza PDF, README e links_publicacao.json.
Ele apenas registra os links, não comprova que estejam acessíveis. Testar os links antes do envio.
