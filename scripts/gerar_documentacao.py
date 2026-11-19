"""Regenera o PDF técnico; opcionalmente registra URLs reais no PDF/README."""
from pathlib import Path
import argparse
import json
import html
from urllib.parse import urlparse
from reportlab.platypus import (BaseDocTemplate, PageTemplate, Frame, Paragraph, Spacer,
    PageBreak, NextPageTemplate, Table, TableStyle, Preformatted, Image, Flowable)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.pagesizes import A4, A3, landscape
from reportlab.lib import colors

ROOT=Path(__file__).resolve().parents[1]
VERDE=colors.HexColor('#175b48');TEXTO=colors.HexColor('#233e35');CLARO=colors.HexColor('#edf4ef')
styles=getSampleStyleSheet()
styles.add(ParagraphStyle(name='TituloEco',fontName='Helvetica-Bold',fontSize=23,leading=28,textColor=VERDE,spaceAfter=16))
styles.add(ParagraphStyle(name='CorpoEco',fontName='Helvetica',fontSize=10.4,leading=14.5,textColor=TEXTO,spaceAfter=10))
styles.add(ParagraphStyle(name='MenorEco',fontName='Helvetica',fontSize=8.6,leading=11.5,textColor=TEXTO,spaceAfter=7))
styles.add(ParagraphStyle(name='CabEco',fontName='Helvetica-Bold',fontSize=9,leading=12,textColor=colors.white))
styles.add(ParagraphStyle(name='CodigoEco',fontName='Courier',fontSize=8.1,leading=10.5,textColor=TEXTO,spaceBefore=5,spaceAfter=9))

def par(s,small=False):return Paragraph(html.escape(str(s)).replace('\n','<br/>'),styles['MenorEco' if small else 'CorpoEco'])
def titulo(s):return Paragraph(html.escape(s),styles['TituloEco'])
def code(s):return Preformatted(s.strip(),styles['CodigoEco'])
def tabela(rows,widths):
    data=[[Paragraph(html.escape(str(c)),styles['CabEco'] if i==0 else styles['MenorEco']) for c in row] for i,row in enumerate(rows)]
    t=Table(data,colWidths=widths,repeatRows=1,hAlign='LEFT')
    t.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),VERDE),('ROWBACKGROUNDS',(0,1),(-1,-1),[CLARO,colors.white]),
        ('VALIGN',(0,0),(-1,-1),'TOP'),('LEFTPADDING',(0,0),(-1,-1),8),('RIGHTPADDING',(0,0),(-1,-1),8),
        ('TOPPADDING',(0,0),(-1,-1),7),('BOTTOMPADDING',(0,0),(-1,-1),6),('LINEBELOW',(0,0),(-1,0),.5,VERDE)]))
    return t

class Classes(Flowable):
    def __init__(self):Flowable.__init__(self);self.width=500;self.height=410
    def draw(self):
        c=self.canv
        def box(x,y,w,h,nome,campos):
            c.setFillColor(CLARO);c.setStrokeColor(VERDE);c.roundRect(x,y,w,h,5,fill=1)
            c.setFillColor(VERDE);c.rect(x,y+h-28,w,28,fill=1,stroke=0)
            c.setFillColor(colors.white);c.setFont('Helvetica-Bold',11);c.drawCentredString(x+w/2,y+h-18,nome)
            c.setFillColor(TEXTO);c.setFont('Helvetica',9)
            for i,linha in enumerate(campos):c.drawString(x+10,y+h-47-17*i,linha)
        box(142,260,216,130,'Residencia',['- cdResidencia : Long','- nmResidencia : String','- qtMoradores : Integer','- vlTarifaKwh : BigDecimal'])
        box(0,15,232,150,'ConsumoDiario',['- cdConsumo : Long','- cdResidencia : Long','- dtConsumo : LocalDate','- vlConsumoKwh : BigDecimal','- dsOrigem : String'])
        box(268,15,232,150,'MetaConsumo',['- cdMeta : Long','- cdResidencia : Long','- dtInicio : LocalDate','- dtFim : LocalDate','- vlLimiteKwh : BigDecimal'])
        c.setStrokeColor(VERDE);c.setLineWidth(1.1);c.line(215,260,116,165);c.line(285,260,384,165)
        c.setFont('Helvetica-Bold',10);c.setFillColor(TEXTO)
        for x,y,s in [(202,241,'1'),(120,178,'0..N'),(292,241,'1'),(359,178,'0..N')]:c.drawString(x,y,s)
        c.setFont('Helvetica',9);c.drawCentredString(250,205,'Vinculo por cdResidencia / chave estrangeira')

def gerar(api_url=None,video_url=None):
    caminho=ROOT/'docs/links_publicacao.json';links=json.loads(caminho.read_text())
    for k,v in [('api_url',api_url),('video_url',video_url)]:
        if v:
            u=urlparse(v)
            if u.scheme!='https' or not u.netloc or u.username or u.password:raise ValueError('Informe URL HTTPS pública, sem credenciais.')
            links[k]=v
    caminho.write_text(json.dumps(links,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    api=links.get('api_url') or 'PENDENTE - API ainda nao publicada no Render'
    video=links.get('video_url') or 'PENDENTE - video ainda nao gravado/publicado'
    if api_url or video_url:
        f=ROOT/'README.md';s=f.read_text(encoding='utf-8')
        import re
        if api_url:s=re.sub(r'- API no Render:.*', '- API no Render: '+api_url,s)
        if video_url:s=re.sub(r'- Vídeo YouTube não listado \(até 5 min\):.*','- Vídeo YouTube não listado (até 5 min): '+video_url,s)
        f.write_text(s,encoding='utf-8')
    out=ROOT/'docs/EcoLar_Java_Documentacao.pdf'
    doc=BaseDocTemplate(str(out),title='EcoLar - Documentação técnica Java',author='Equipe EcoLar',leftMargin=42,rightMargin=42,topMargin=56,bottomMargin=42)
    def pagina(canvas,documento):
        w,h=canvas._pagesize;canvas.saveState();canvas.setFillColor(VERDE);canvas.setFont('Helvetica-Bold',8)
        canvas.drawString(42,h-29,'ECOLAR / DOMAIN DRIVEN DESIGN USING JAVA');canvas.setFont('Helvetica',8);canvas.drawRightString(w-42,h-29,'FIAP - GS 2026/2')
        canvas.setStrokeColor(colors.HexColor('#ccdcd2'));canvas.line(42,h-38,w-42,h-38)
        canvas.setFillColor(TEXTO);canvas.setFont('Helvetica',7.5);canvas.drawString(42,22,'Documento tecnico - revisar links e demonstracao antes do Portal');canvas.drawRightString(w-42,22,str(documento.page));canvas.restoreState()
    templates=[]
    for name,size in [('P',A4),('L',landscape(A3))]:
        w,h=size;templates.append(PageTemplate(id=name,pagesize=size,frames=[Frame(42,42,w-84,h-98,id=name,leftPadding=0,rightPadding=0,topPadding=0,bottomPadding=0)],onPage=pagina))
    doc.addPageTemplates(templates);s=[]
    def nova(nome,conteudo,layout='P'):
        if s:s.extend([NextPageTemplate(layout),PageBreak()])
        s.append(titulo(nome));s.extend(conteudo)
    nova('EcoLar\nAPI Java com Oracle',[Spacer(1,25),par('Eficiência energética residencial: histórico diário, metas e informações para o morador.'),
        tabela([['Integrante','RM'],['Yan Almeida','568814'],['Vinicius Pacheco Ruiz','571109'],['Diego Barbosa','568829']],[350,160]),Spacer(1,18),
        par('Turma 1TDSPV | Global Solution 2026/2 | Versão 1.0.0 | Preparação: 16/11/2026.'),
        par('API no Render: '+api),par('Vídeo YouTube (até 5 minutos): '+video),
        Spacer(1,12),par('Estado desta versão: implementação e revisão local. Confirmar Oracle FIAP, integrar o front React, publicar e registrar os links reais. Não há afirmação de deploy ou vídeo concluídos.'),
        par('Rubrica: enunciado FIAP fornecido pelo grupo, páginas 47-56. A entrega exige ZIP com projeto, SQL, Dockerfile, PDF e README com links. Este PDF acompanha o código; não substitui a demonstração.',True)])
    nova('01 / Problema e escopo',[par('O responsável pelo orçamento doméstico precisa acompanhar o consumo e compreender variações sem tratar uma conta mensal como medição diária. EcoLar organiza residências, registros em kWh por dia e metas por intervalo.'),
        tabela([['Parte','Implementação'],['Residência','CRUD de nome, moradores e tarifa informada.'],['Consumo diário','CRUD por residência/data; origem MANUAL, IMPORTADO ou SIMULADO.'],['Meta de consumo','CRUD por intervalo inclusivo e limite em kWh.'],['Informação ao cidadão','Total, média por dia registrado, cobertura e custo estimado.'],['IA','Solicitação de previsão e agrupamento ao Flask; histórico persistido após validação.']],[120,390]),Spacer(1,12),
        par('Arquitetura: front -> HTTP/JSON -> controller/DTO -> service -> DAO JDBC -> Oracle FIAP. A integração de IA faz Java -> Flask -> modelo treinado. DTOs impedem que models sejam recebidos ou devolvidos diretamente.'),
        par('As tabelas de aparelhos e vínculos são preservadas no SQL do grupo, mas não entram no CRUD desta frente web. Previsões e análises são históricos de inferência, apenas inclusão e consulta. A entrega procedural de Python mantém seu JSON independente.')])
    nova('02 / Diagrama de classes',[Classes(),par('As três entidades de CRUD possuem associação 1:N via cdResidencia. A representação Java usa o identificador da residência, e o banco impõe a FK. Não há mapeamento JPA ou carregamento de listas por ORM.'),
        par('POJOs têm construtor padrão, atributos privados e getters/setters. ModelMapper converte os DTOs de entrada em models e models em DTOs de saída. DTOs de resultados adicionais são projeções imutáveis do histórico.',True)])
    der=ROOT/'docs/DER_Oracle.png'
    img=Image(str(der));escala=min(1100/img.imageWidth,620/img.imageHeight);img.drawWidth=img.imageWidth*escala;img.drawHeight=img.imageHeight*escala
    nova('03 / Modelo relacional Oracle',[par('DER do Data Modeler fornecido pelo grupo. O schema existente é preservado: sete tabelas, sete sequences e relacionamento 1:N de residência com consumo e meta.'),img,Spacer(1,12),
        par('PKs: cd_residencia, cd_consumo e cd_meta. FKs de tb_consumo_diario e tb_meta_consumo: cd_residencia -> tb_residencia. Unicidade: residência + data em consumo. Java adiciona a regra de não sobrepor metas; o SQL original permanece compatível.'),
        par('sql/01_estrutura_oracle.sql é apenas para schema vazio. Não executar sobre o banco já criado. A carga opcional contém os 60 registros sintéticos originais; resultados fixtures não são renomeados para MODELO.',True)],'L')
    nova('04 / Organização do projeto',[tabela([['Pacote/arquivo','Responsabilidade'],['model','Residencia, ConsumoDiario e MetaConsumo: entidades do domínio.'],['dto','Entradas com Bean Validation, saídas e projeções de cálculos/IA.'],['dao','Um DAO por entidade, DataSource por construtor e PreparedStatement. IaRegistroDao insere/consulta inferências.'],['service','Transações, regras, resumos e integração com Flask.'],['controller','Rotas, DTOs, @Valid, status e Location.'],['exception / handler','Exceções próprias e @RestControllerAdvice; erro JSON sem SQL/stack trace.'],['config','ModelMapper estrito, Clock, HttpClient e CORS por origens.'],['resources','YAML de configuração e console HTML de teste com Fetch real.'],['test','JDBC em H2 modo Oracle; teste HTTP opcional com Flask real.'],['Dockerfile / render.yaml','Build multi-stage e variáveis externas para publicação.']],[135,375]),Spacer(1,12),
        par('JDBC usa DataSourceUtils para participar da mesma transação Spring. ResultSet e PreparedStatement são fechados; conexões são liberadas sem encerrar antes do commit uma conexão vinculada à transação. SQL é explícito; JPA/Hibernate/Spring Data não são dependências do projeto.')])
    rows=[['Método','Rota','Sucesso']]
    for rota in ['residencias','consumos','metas']:
        rows += [['POST','/'+rota,'201 + Location'],['GET','/'+rota,'200 (array)'],['GET','/'+rota+'/{id}','200'],['PUT','/'+rota+'/{id}','200'],['DELETE','/'+rota+'/{id}','204 sem corpo']]
    nova('05 / Endpoints de CRUD',[tabela(rows,[60,290,160]),Spacer(1,12),par('400: entrada/validação. 404: ID ausente. 409: duplicidade, sobreposição ou remoção de pai com dependentes. 405: método incompatível, com Allow. Os GETs de listagem retornam array vazio quando não há registros.'),par('Entradas e saídas completas e coleção importável: docs/API.md e postman/EcoLar_Java.postman_collection.json.',True)])
    nova('06 / Rotas de relacionamento e análise',[tabela([['Método','Rota / resultado'],['GET / POST','/residencias/{id}/consumos - lista ou cadastra filho.'],['GET / POST','/residencias/{id}/metas - lista ou cadastra filho.'],['GET','/residencias/{id}/resumo?inicio=AAAA-MM-DD&fim=AAAA-MM-DD'],['GET','/metas/{id}/acompanhamento'],['POST / GET','/residencias/{id}/previsoes - gera ou lista inferências.'],['GET','/previsoes/{id}'],['POST / GET','/residencias/{id}/analises - gera ou lista inferências.'],['GET','/analises/{id}'],['GET','/health/live - processo em execução.'],['GET','/health - testa conexão JDBC; 200 ou 503.']],[90,420]),Spacer(1,12),
        par('POSTs aninhados usam o ID da rota; não exigem cdResidencia no body. POST de filho aponta Location para sua rota própria. Pai inexistente devolve 404. Solicitações de IA usam dataReferencia do último dia observado, com sete registros consecutivos no Oracle.'),
        code('{\n  "dataReferencia": "2026-10-14"\n}'),par('As inferências são imutáveis nesta API: inclusão e consulta preservam histórico. Não existem PUT/DELETE de previsões e análises. O CRUD completo avaliado é o das três entidades do domínio.')])
    nova('07 / Exemplos de entrada e saída',[par('POST /residencias - entrada:'),code(json.dumps({'nmResidencia':'Casa de teste','qtMoradores':3,'vlTarifaKwh':1.0},ensure_ascii=False,indent=2)),
        par('Resposta 201 e Location: /residencias/{id}; ID abaixo é ilustrativo:'),code(json.dumps({'cdResidencia':1000,'nmResidencia':'Casa de teste','qtMoradores':3,'vlTarifaKwh':1.0},ensure_ascii=False,indent=2)),
        par('POST /residencias/{id}/consumos - entrada:'),code(json.dumps({'dtConsumo':'2020-01-01','vlConsumoKwh':6.2,'dsOrigem':'MANUAL'},indent=2)),
        par('Resposta 201; saída contém também cdConsumo e cdResidencia. No POST /consumos ou PUT /consumos/{id}, enviar cdResidencia no body. IDs novos são gerados por sequence, nunca pelo cliente.')])
    nova('08 / Metas e validações',[par('POST /residencias/{id}/metas - entrada:'),code(json.dumps({'dtInicio':'2020-01-01','dtFim':'2020-01-31','vlLimiteKwh':180},indent=2)),
        par('Saída 201 inclui cdMeta e cdResidencia. No POST /metas ou PUT /metas/{id}, enviar também cdResidencia.'),
        tabela([['DTO','Validações'],['Residência','@NotBlank/@Size(100) no nome; moradores @Min(1)/@Max(9999); tarifa @DecimalMin(0)/@Digits(6,4).'],['Consumo','Casa positiva; data @NotNull/@PastOrPresent; kWh 0-10000 com até três decimais; origem MANUAL/IMPORTADO/SIMULADO.'],['Meta','Casa positiva; datas obrigatórias; limite positivo com até nove inteiros e três decimais. Fim/início e sobreposição validados no service.'],['IA','dataReferencia obrigatória e não futura; histórico completo conferido no banco.']],[100,410]),Spacer(1,10),par('Controllers ativam @Valid; IDs de rota são positivos. Campos desconhecidos, tipos incorretos e inteiros fracionários são rejeitados. Métodos PUT não permitem transferir um consumo ou meta para outra residência.')])
    nova('09 / Erros e contrato HTTP',[par('Formato único de erro, sem detalhes de SQL, credenciais ou stack trace:'),
        code(json.dumps({'timestamp':'2026-11-16T18:00:00-03:00','status':404,'codigo':'nao_encontrado','mensagem':'Residência não encontrada.','caminho':'/residencias/999999','campos':{}},ensure_ascii=False,indent=2)),
        tabela([['Status','Situação'],['400','JSON/tipo/parâmetro inválido; falha de Bean Validation ou período invertido.'],['404','Residência, consumo, meta, inferência ou rota ausente.'],['405','Método não permitido; cabeçalho Allow.'],['409','Regra de negócio ou dependências impedem a operação.'],['415 / 406','Content-Type ou Accept incompatível com JSON.'],['500','Falha inesperada; mensagem genérica.'],['502 / 503 / 504','Saída de IA inválida / serviço ou banco indisponível / timeout de IA.']],[75,435]),Spacer(1,10),par('Exceções próprias: EntradaInvalidaException, RecursoNaoEncontradoException, RegraNegocioException, PersistenciaException e IaException. O handler converte para o contrato público sem repassar mensagens do driver.')])
    nova('10 / Regras do domínio',[tabela([['Regra','Como é aplicada'],['Consumo único','Residência + data. Conferência no service e UNIQUE no banco.'],['Metas sem sobreposição','Intervalos inclusivos: início <= fim existente e fim >= início existente. Conflito 409.'],['Concorrência','Escritas obtêm SELECT FOR UPDATE na residência dentro de transação, serializando regras da mesma casa.'],['Remoção do pai','Bloqueada se houver consumo, meta, aparelho vinculado ou histórico de IA. Sem exclusão em cascata.'],['Consumo diário','Não é potência nem leitura acumulada. Datas futuras e valores negativos são inválidos.'],['Cobertura','Dias registrados / dias do intervalo. Dias ausentes não viram zero.'],['Média e custo','Média usa dias registrados; custo = total x tarifa atual. Não calcula fatura oficial.'],['Situação da meta','SEM DADOS; LIMITE ULTRAPASSADO; PARCIAL; NO LIMITE; DENTRO DO LIMITE.']],[120,390]),Spacer(1,12),
        par('Exemplo: 10 kWh no dia 1 e 20 kWh no dia 3, intervalo de três dias: total 30, média registrada 15 e cobertura 66,67%. Não se afirma que o dia 2 teve consumo zero. As regras foram preservadas em relação ao módulo Python do grupo.')])
    nova('11 / Integração com os modelos de IA',[par('Fluxo implementado: validar residência/data -> consultar sete dias no Oracle -> ordenar e conferir lacunas -> enviar JSON para Flask -> validar saída -> inserir registro com sequence -> devolver DTO e avisos.'),
        code('{\n  "historico": [\n    {"data": "2020-01-01", "consumo_kwh": 6.2},\n    "... outros seis registros, em ordem ..."\n  ]\n}'),
        par('O trecho acima ilustra o formato; a implementação envia sete objetos reais, sem a string abreviada. /prever retorna o próximo dia; /agrupar retorna grupo e interpretação da janela. Não são devolvidos números fixos pelo Java.'),
        tabela([['Controle','Comportamento'],['Histórico insuficiente','409, sem chamar o modelo.'],['HTTP e timeout','HttpClient; conexão 3 s e pedido 12 s. Sem seguir redirects.'],['Validação da saída','Datas exatas, origem MODELO, valor não negativo, grupo inteiro e limites das colunas.'],['Persistência','tb_previsao_consumo ou tb_analise_padrao, sem modificar fixtures existentes.'],['Avisos','POST preserva avisos do Flask e acrescenta alerta de entradas SIMULADO. A lista não tem coluna própria no schema atual.']],[105,405]),Spacer(1,12),
        par('Limite: modelos treinados numa residência francesa histórica, sem validação de generalização para residências brasileiras. Agrupamento não comprova desperdício ou causa; previsão não garante economia.')])
    nova('12 / Configuração e execução',[tabela([['Variável','Finalidade'],['ORACLE_URL','JDBC com host, porta e serviço/SID; sem credenciais embutidas.'],['ORACLE_USER','Usuário do schema.'],['ORACLE_PASSWORD','Senha no ambiente; nunca no código, PDF ou ZIP.'],['CORS_ORIGINS','Origens do front separadas por vírgulas.'],['IA_BASE_URL','Serviço Flask acessível pelo processo Java.'],['PORT','Porta fornecida pelo Render; padrão local 8080.']],[130,380]),Spacer(1,14),
        par('JDK 21 de 64 bits. O Maven Wrapper fixa Maven 3.9.11 e baixa a distribuição na primeira compilação. Spring Boot 3.5.16, ModelMapper 3.2.4 e driver ojdbc11 estão declarados no pom.'),
        code('Windows:\n  INICIAR_JAVA_WINDOWS.bat\n  mvnw.cmd test\n  COMPILAR_WINDOWS.bat\n\nLinux/macOS:\n  ./mvnw verify\n  java -jar bin/ecolar-api.jar'),
        par('O script Windows solicita a URL JDBC, usuário e senha. A senha fica no processo e não é salva em arquivo. O JAR local evita instalar Maven apenas para iniciar. Se alterar o código, recompile e atualize o JAR. O perfil padrão usa exclusivamente Oracle; H2 existe só no escopo de teste.')])
    nova('13 / Deploy e links obrigatórios',[par('API: '+api),par('Vídeo: '+video),
        par('Configuração preparada: Dockerfile multi-stage compila com Maven/JDK 21 e executa com JRE 21, usuário sem privilégios. Render usa PORT e health check /health. O banco é externo; Java não recria tabelas.'),
        par('Passos para publicar: repositório sem segredos -> Web Service Docker no Render -> configurar variáveis -> validar rede até Oracle FIAP -> conferir /health e CRUD -> liberar a origem do React -> registrar URL real.'),
        par('Um Flask em 127.0.0.1 no computador do aluno não pode ser acessado pelo Java no Render. Para demonstrar IA remotamente, disponibilizar o serviço Flask por URL acessível. Os CRUDs não precisam da IA para funcionar.'),
        par('O front React do grupo deve listar e cadastrar dados por Fetch na API real. O console HTML incluído é de diagnóstico; não substitui essa integração. Vídeo: até cinco minutos, YouTube não listado, com o front funcionando e explicação do sistema.'),
        par('Não foi realizada publicação pelo preparador nem fornecida URL/vídeo nesta versão. URLs informadas ao gerador são apenas registradas: o script não comprova acesso remoto. Detalhes e etapas em docs/DEPLOY.md e docs/FINALIZACAO.md.')])
    validacao=ROOT/'docs/resumo_validacao.json'
    v=json.loads(validacao.read_text()) if validacao.exists() else {}
    nova('14 / Testes, evidências e limites',[tabela([['Verificação','Estado registrado'],['Compilação','JDK 21 / Maven; resultado em docs/VALIDACAO.md.'],['Testes principais',str(v.get('testes_principais','Consultar log final'))+' casos: CRUD, regras, erros, CORS, concorrência e JDBC local.'],['Integração Flask real',str(v.get('flask_real','Consultar log final'))],['Persistência dos testes','H2 em modo Oracle com DDL de teste adaptado. Não substitui teste na conta FIAP.'],['Oracle FIAP','Pendente de configuração/teste pelo grupo. Sem credenciais fornecidas ao preparador.'],['Windows','Scripts preparados; execução no computador do grupo ainda deve ser validada.'],['Render, front e vídeo','Pendentes de execução e links/evidências reais.']],[140,370]),Spacer(1,12),
        par('Os testes padrão usam um dublê HTTP da IA explicitamente identificado para validar falhas e contrato. O teste opcional IaRealIntegrationTest usa HTTP real com os modelos exportados do EcoLar. Não são atribuídas evidências do Oracle a um teste em memória.'),
        par('Revisar identificação, conteúdo e código antes de enviar. Todos os integrantes devem conseguir explicar o caminho controller -> DTO/service -> DAO -> Oracle. O ZIP não deve incluir target, credenciais ou caches. Entrega prevista no enunciado: 19/11/2026 às 23h55; apresentação 23 a 27/11.')])
    nova('15 / Fontes e responsabilidade',[par('1. FIAP. 1TDS Fevereiro - Global Solution 2026-2. Enunciado fornecido pelo grupo; Java pp. 47-56. Referência dos requisitos, formato, vídeo e pontuação.'),
        par('2. Equipe EcoLar. SQL Oracle, modelo relacional, Business Model e entrega de IA recebidos nesta conversa. Fonte do schema, das regras e do contrato Flask.'),
        par('3. Spring Boot: https://docs.spring.io/spring-boot/3.5/system-requirements.html'),
        par('4. Oracle JDBC: https://www.oracle.com/database/technologies/maven-central-guide.html'),
        par('5. ModelMapper: https://modelmapper.org/user-manual/configuration/'),
        par('6. Render: https://render.com/docs/docker e https://render.com/docs/health-checks'),
        par('Fontes técnicas consultadas na preparação em 16/11/2026. Dependências fixadas no pom.xml e ambiente efetivamente testado descrito no registro de validação.'),
        par('ChatGPT/Codex auxiliou análise, implementação, testes e documentação específica do EcoLar, conforme docs/USO_DE_IA.md. Não foram inventadas gravações, links públicos, credenciais ou testes na conta FIAP. A revisão humana e o domínio técnico do grupo não são substituídos pelos testes automatizados.')])
    doc.build(s)
    print('PDF gerado:',out)

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--api-url');parser.add_argument('--video-url');args=parser.parse_args();gerar(args.api_url,args.video_url)
