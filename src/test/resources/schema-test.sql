CREATE TABLE tb_residencia (
    cd_residencia NUMBER(10) NOT NULL,
    nm_residencia VARCHAR2(100) NOT NULL,
    qt_moradores NUMBER(4) NOT NULL,
    vl_tarifa_kwh NUMBER(10,4) NOT NULL,
    CONSTRAINT pk_residencia PRIMARY KEY (cd_residencia),
    CONSTRAINT ck_res_moradores CHECK (qt_moradores > 0),
    CONSTRAINT ck_res_tarifa CHECK (vl_tarifa_kwh >= 0)
);






CREATE SEQUENCE sq_tb_residencia START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_aparelho (
    cd_aparelho NUMBER(10) NOT NULL,
    nm_aparelho VARCHAR2(100) NOT NULL,
    vl_potencia_w NUMBER(10,2) NOT NULL,
    CONSTRAINT pk_aparelho PRIMARY KEY (cd_aparelho),
    CONSTRAINT uk_aparelho_nome UNIQUE (nm_aparelho),
    CONSTRAINT ck_apa_potencia CHECK (vl_potencia_w > 0)
);





CREATE SEQUENCE sq_tb_aparelho START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_consumo_diario (
    cd_consumo NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_consumo DATE NOT NULL,
    vl_consumo_kwh NUMBER(12,3) NOT NULL,
    ds_origem VARCHAR2(10) NOT NULL,
    CONSTRAINT pk_consumo_diario PRIMARY KEY (cd_consumo),
    CONSTRAINT fk_con_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT uk_con_res_data UNIQUE (cd_residencia, dt_consumo),
    CONSTRAINT ck_con_valor CHECK (vl_consumo_kwh >= 0),
    CONSTRAINT ck_con_data CHECK (dt_consumo = TRUNC(dt_consumo)),
    CONSTRAINT ck_con_origem CHECK (ds_origem IN ('MANUAL', 'IMPORTADO', 'SIMULADO'))
);







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







CREATE SEQUENCE sq_tb_residencia_aparelho START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_previsao_consumo (
    cd_previsao NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_referencia DATE NOT NULL,
    dt_alvo DATE NOT NULL,
    vl_previsto_kwh NUMBER(12,3) NOT NULL,
    ds_modelo VARCHAR2(80) NOT NULL,
    dt_geracao DATE NOT NULL,
    ds_origem VARCHAR2(10) NOT NULL,
    CONSTRAINT pk_previsao_consumo PRIMARY KEY (cd_previsao),
    CONSTRAINT fk_pre_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT ck_pre_valor CHECK (vl_previsto_kwh >= 0),
    CONSTRAINT ck_pre_datas CHECK (dt_alvo = dt_referencia + 1),
    CONSTRAINT ck_pre_dias CHECK (dt_alvo = TRUNC(dt_alvo) AND dt_referencia = TRUNC(dt_referencia)),
    CONSTRAINT ck_pre_origem CHECK (ds_origem IN ('MODELO', 'SIMULADO'))
);










CREATE SEQUENCE sq_tb_previsao_consumo START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE tb_analise_padrao (
    cd_analise NUMBER(10) NOT NULL,
    cd_residencia NUMBER(10) NOT NULL,
    dt_inicio DATE NOT NULL,
    dt_fim DATE NOT NULL,
    cd_grupo NUMBER(4) NOT NULL,
    ds_interpretacao VARCHAR2(500) NOT NULL,
    ds_modelo VARCHAR2(80) NOT NULL,
    ds_origem VARCHAR2(10) NOT NULL,
    CONSTRAINT pk_analise_padrao PRIMARY KEY (cd_analise),
    CONSTRAINT fk_ana_residencia FOREIGN KEY (cd_residencia) REFERENCES tb_residencia (cd_residencia),
    CONSTRAINT ck_ana_janela CHECK (dt_fim = dt_inicio + 6),
    CONSTRAINT ck_ana_dias CHECK (dt_inicio = TRUNC(dt_inicio) AND dt_fim = TRUNC(dt_fim)),
    CONSTRAINT ck_ana_grupo CHECK (cd_grupo >= 0),
    CONSTRAINT ck_ana_origem CHECK (ds_origem IN ('MODELO', 'SIMULADO'))
);










CREATE SEQUENCE sq_tb_analise_padrao START WITH 1000 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE INDEX ix_meta_res ON tb_meta_consumo (cd_residencia);

CREATE INDEX ix_vinculo_aparelho ON tb_residencia_aparelho (cd_aparelho);

CREATE INDEX ix_prev_res ON tb_previsao_consumo (cd_residencia);

CREATE INDEX ix_analise_res ON tb_analise_padrao (cd_residencia);
