-- ECOLAR JAVA: estrutura Oracle existente do grupo.
-- SOMENTE para schema vazio. Nao executar em banco ja criado. Sem DROP.
-- Oracle SQL Developer: F5. Java nunca cria nem recria tabelas automaticamente.
SET DEFINE OFF
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
CREATE TABLE tb_residencia (
    cd_residencia NUMBER(10) NOT NULL,
    nm_residencia VARCHAR2(100 CHAR) NOT NULL,
    qt_moradores NUMBER(4) NOT NULL,
    vl_tarifa_kwh NUMBER(10,4) NOT NULL,
    CONSTRAINT pk_residencia PRIMARY KEY (cd_residencia),
    CONSTRAINT ck_res_moradores CHECK (qt_moradores > 0),
    CONSTRAINT ck_res_tarifa CHECK (vl_tarifa_kwh >= 0)
);

COMMENT ON TABLE tb_residencia IS 'Residencia acompanhada no prototipo EcoLar.';

COMMENT ON COLUMN tb_residencia.cd_residencia IS 'Identificador da residencia.';

COMMENT ON COLUMN tb_residencia.nm_residencia IS 'Nome de exibicao; ficticio no MVP.';

COMMENT ON COLUMN tb_residencia.qt_moradores IS 'Quantidade inteira positiva de moradores.';

COMMENT ON COLUMN tb_residencia.vl_tarifa_kwh IS 'Tarifa informada em reais por kWh; nao e tarifa oficial.';

CREATE SEQUENCE sq_tb_residencia START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_aparelho (
    cd_aparelho NUMBER(10) NOT NULL,
    nm_aparelho VARCHAR2(100 CHAR) NOT NULL,
    vl_potencia_w NUMBER(10,2) NOT NULL,
    CONSTRAINT pk_aparelho PRIMARY KEY (cd_aparelho),
    CONSTRAINT uk_aparelho_nome UNIQUE (nm_aparelho),
    CONSTRAINT ck_apa_potencia CHECK (vl_potencia_w > 0)
);

COMMENT ON TABLE tb_aparelho IS 'Catalogo de tipos ou modelos de aparelhos.';

COMMENT ON COLUMN tb_aparelho.cd_aparelho IS 'Identificador do aparelho no catalogo.';

COMMENT ON COLUMN tb_aparelho.nm_aparelho IS 'Nome do tipo ou modelo do aparelho.';

COMMENT ON COLUMN tb_aparelho.vl_potencia_w IS 'Potencia nominal em watts para simulacao.';

CREATE SEQUENCE sq_tb_aparelho START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_consumo_diario (
    cd_consumo NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_consumo DATE NOT NULL,
    vl_consumo_kwh NUMBER(12,3) NOT NULL,
    ds_origem VARCHAR2(10 CHAR) NOT NULL,
    CONSTRAINT pk_consumo_diario PRIMARY KEY (cd_consumo),
    CONSTRAINT fk_con_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT uk_con_res_data UNIQUE (cd_residencia, dt_consumo),
    CONSTRAINT ck_con_valor CHECK (vl_consumo_kwh >= 0),
    CONSTRAINT ck_con_data CHECK (dt_consumo = TRUNC(dt_consumo)),
    CONSTRAINT ck_con_origem CHECK (ds_origem IN ('MANUAL', 'IMPORTADO', 'SIMULADO'))
);

COMMENT ON TABLE tb_consumo_diario IS 'Energia diaria registrada; dados de teste identificados por origem.';

COMMENT ON COLUMN tb_consumo_diario.cd_consumo IS 'Identificador do consumo diario.';

COMMENT ON COLUMN tb_consumo_diario.cd_residencia IS 'Residencia dona do registro.';

COMMENT ON COLUMN tb_consumo_diario.dt_consumo IS 'Dia do consumo, armazenado sem horario.';

COMMENT ON COLUMN tb_consumo_diario.vl_consumo_kwh IS 'Energia consumida no dia em kWh; nao e potencia nem leitura acumulada.';

COMMENT ON COLUMN tb_consumo_diario.ds_origem IS 'MANUAL, IMPORTADO ou SIMULADO.';

CREATE SEQUENCE sq_tb_consumo_diario START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_meta_consumo (
    cd_meta NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_inicio DATE NOT NULL,
    dt_fim DATE NOT NULL,
    vl_limite_kwh NUMBER(12,3) NOT NULL,
    CONSTRAINT pk_meta_consumo PRIMARY KEY (cd_meta),
    CONSTRAINT fk_met_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT ck_met_limite CHECK (vl_limite_kwh > 0),
    CONSTRAINT ck_met_periodo CHECK (dt_fim >= dt_inicio),
    CONSTRAINT ck_met_dias CHECK (dt_inicio = TRUNC(dt_inicio) AND dt_fim = TRUNC(dt_fim))
);

COMMENT ON TABLE tb_meta_consumo IS 'Limite de consumo desejado para um intervalo.';

COMMENT ON COLUMN tb_meta_consumo.cd_meta IS 'Identificador da meta.';

COMMENT ON COLUMN tb_meta_consumo.cd_residencia IS 'Residencia acompanhada pela meta.';

COMMENT ON COLUMN tb_meta_consumo.dt_inicio IS 'Primeiro dia do intervalo; sem horario.';

COMMENT ON COLUMN tb_meta_consumo.dt_fim IS 'Ultimo dia do intervalo, inclusivo; sem horario.';

COMMENT ON COLUMN tb_meta_consumo.vl_limite_kwh IS 'Limite positivo de energia em kWh para todo o intervalo.';

CREATE SEQUENCE sq_tb_meta_consumo START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_residencia_aparelho (
    cd_vinculo NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    cd_aparelho NUMBER(10) NOT NULL,
    qt_aparelhos NUMBER(4) NOT NULL,
    vl_horas_dia NUMBER(5,2) NOT NULL,
    CONSTRAINT pk_residencia_aparelho PRIMARY KEY (cd_vinculo),
    CONSTRAINT fk_vin_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT fk_vin_aparelho FOREIGN KEY (cd_aparelho) REFERENCES tb_aparelho (cd_aparelho),
    CONSTRAINT uk_vin_res_aparelho UNIQUE (cd_residencia, cd_aparelho),
    CONSTRAINT ck_vin_quantidade CHECK (qt_aparelhos > 0),
    CONSTRAINT ck_vin_horas CHECK (vl_horas_dia BETWEEN 0 AND 24)
);

COMMENT ON TABLE tb_residencia_aparelho IS 'Associativa N:N; uso estimado de um aparelho em uma residencia.';

COMMENT ON COLUMN tb_residencia_aparelho.cd_vinculo IS 'Identificador do vinculo residencia-aparelho.';

COMMENT ON COLUMN tb_residencia_aparelho.cd_residencia IS 'Residencia que utiliza o aparelho.';

COMMENT ON COLUMN tb_residencia_aparelho.cd_aparelho IS 'Tipo ou modelo presente no catalogo.';

COMMENT ON COLUMN tb_residencia_aparelho.qt_aparelhos IS 'Quantidade inteira positiva desse aparelho na residencia.';

COMMENT ON COLUMN tb_residencia_aparelho.vl_horas_dia IS 'Tempo medio estimado de uso diario entre 0 e 24 horas.';

CREATE SEQUENCE sq_tb_residencia_aparelho START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_previsao_consumo (
    cd_previsao NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_referencia DATE NOT NULL,
    dt_alvo DATE NOT NULL,
    vl_previsto_kwh NUMBER(12,3) NOT NULL,
    ds_modelo VARCHAR2(80 CHAR) NOT NULL,
    dt_geracao DATE NOT NULL,
    ds_origem VARCHAR2(10 CHAR) NOT NULL,
    CONSTRAINT pk_previsao_consumo PRIMARY KEY (cd_previsao),
    CONSTRAINT fk_pre_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT ck_pre_valor CHECK (vl_previsto_kwh >= 0),
    CONSTRAINT ck_pre_datas CHECK (dt_alvo = dt_referencia + 1),
    CONSTRAINT ck_pre_dias CHECK (dt_alvo = TRUNC(dt_alvo) AND dt_referencia = TRUNC(dt_referencia)),
    CONSTRAINT ck_pre_origem CHECK (ds_origem IN ('MODELO', 'SIMULADO'))
);

COMMENT ON TABLE tb_previsao_consumo IS 'Previsao de proximo dia; fixtures nunca representam modelo treinado.';

COMMENT ON COLUMN tb_previsao_consumo.cd_previsao IS 'Identificador da previsao.';

COMMENT ON COLUMN tb_previsao_consumo.cd_residencia IS 'Residencia analisada.';

COMMENT ON COLUMN tb_previsao_consumo.dt_referencia IS 'Ultimo dia observado utilizado na analise; sem horario.';

COMMENT ON COLUMN tb_previsao_consumo.dt_alvo IS 'Dia futuro ao de referencia para o qual se estima consumo.';

COMMENT ON COLUMN tb_previsao_consumo.vl_previsto_kwh IS 'Energia estimada pelo modelo, ou fixture identificada.';

COMMENT ON COLUMN tb_previsao_consumo.ds_modelo IS 'Nome e versao do modelo; FIXTURE_NAO_TREINADO nos exemplos.';

COMMENT ON COLUMN tb_previsao_consumo.dt_geracao IS 'Momento de geracao do resultado, com horario.';

COMMENT ON COLUMN tb_previsao_consumo.ds_origem IS 'MODELO para inferencia real ou SIMULADO para fixture.';

CREATE SEQUENCE sq_tb_previsao_consumo START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_analise_padrao (
    cd_analise NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_inicio DATE NOT NULL,
    dt_fim DATE NOT NULL,
    cd_grupo NUMBER(4) NOT NULL,
    ds_interpretacao VARCHAR2(500 CHAR) NOT NULL,
    ds_modelo VARCHAR2(80 CHAR) NOT NULL,
    ds_origem VARCHAR2(10 CHAR) NOT NULL,
    CONSTRAINT pk_analise_padrao PRIMARY KEY (cd_analise),
    CONSTRAINT fk_ana_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT ck_ana_janela CHECK (dt_fim = dt_inicio + 6),
    CONSTRAINT ck_ana_dias CHECK (dt_inicio = TRUNC(dt_inicio) AND dt_fim = TRUNC(dt_fim)),
    CONSTRAINT ck_ana_grupo CHECK (cd_grupo >= 0),
    CONSTRAINT ck_ana_origem CHECK (ds_origem IN ('MODELO', 'SIMULADO'))
);

COMMENT ON TABLE tb_analise_padrao IS 'Agrupamento de uma janela semanal; fixtures identificadas.';

COMMENT ON COLUMN tb_analise_padrao.cd_analise IS 'Identificador da analise.';

COMMENT ON COLUMN tb_analise_padrao.cd_residencia IS 'Residencia analisada.';

COMMENT ON COLUMN tb_analise_padrao.dt_inicio IS 'Inicio da janela de sete dias; sem horario.';

COMMENT ON COLUMN tb_analise_padrao.dt_fim IS 'Fim inclusivo da janela; seis dias apos o inicio.';

COMMENT ON COLUMN tb_analise_padrao.cd_grupo IS 'Rotulo numerico nao negativo gerado pelo agrupamento.';

COMMENT ON COLUMN tb_analise_padrao.ds_interpretacao IS 'Interpretacao documentada do grupo, sem presumir causalidade.';

COMMENT ON COLUMN tb_analise_padrao.ds_modelo IS 'Nome/versao do agrupamento; fixture identificada nos testes.';

COMMENT ON COLUMN tb_analise_padrao.ds_origem IS 'MODELO para inferencia real ou SIMULADO para fixture.';

CREATE SEQUENCE sq_tb_analise_padrao START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE INDEX ix_meta_res ON tb_meta_consumo (cd_residencia);

CREATE INDEX ix_vinculo_aparelho ON tb_residencia_aparelho (cd_aparelho);

CREATE INDEX ix_prev_res ON tb_previsao_consumo (cd_residencia);

CREATE INDEX ix_analise_res ON tb_analise_padrao (cd_residencia);

