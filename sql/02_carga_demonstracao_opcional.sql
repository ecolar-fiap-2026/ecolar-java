-- OPCIONAL: apenas schema vazio DEPOIS de 01. Nao repetir em banco populado.
-- 60 registros sinteticos originais. Resultados de IA sao fixtures SIMULADO.
SET DEFINE OFF
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
INSERT INTO tb_residencia (cd_residencia, nm_residencia, qt_moradores, vl_tarifa_kwh)
VALUES (1, 'Casa Aurora - demonstracao', 3, 1.0);

INSERT INTO tb_residencia (cd_residencia, nm_residencia, qt_moradores, vl_tarifa_kwh)
VALUES (2, 'Casa Horizonte - demonstracao', 2, 0.9);

INSERT INTO tb_residencia (cd_residencia, nm_residencia, qt_moradores, vl_tarifa_kwh)
VALUES (3, 'Casa Jardim - demonstracao', 4, 1.1);

INSERT INTO tb_aparelho (cd_aparelho, nm_aparelho, vl_potencia_w)
VALUES (1, 'Chuveiro eletrico 5500W', 5500);

INSERT INTO tb_aparelho (cd_aparelho, nm_aparelho, vl_potencia_w)
VALUES (2, 'Televisao 100W', 100);

INSERT INTO tb_aparelho (cd_aparelho, nm_aparelho, vl_potencia_w)
VALUES (3, 'Computador 300W', 300);

INSERT INTO tb_aparelho (cd_aparelho, nm_aparelho, vl_potencia_w)
VALUES (4, 'Lampada LED 9W', 9);

INSERT INTO tb_aparelho (cd_aparelho, nm_aparelho, vl_potencia_w)
VALUES (5, 'Ventilador 80W', 80);

INSERT INTO tb_aparelho (cd_aparelho, nm_aparelho, vl_potencia_w)
VALUES (6, 'Micro-ondas 1200W', 1200);

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (1, 1, DATE '2026-10-01', 6.2, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (2, 1, DATE '2026-10-02', 6.5, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (3, 1, DATE '2026-10-03', 7.1, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (4, 1, DATE '2026-10-04', 8.0, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (5, 1, DATE '2026-10-05', 6.9, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (6, 1, DATE '2026-10-06', 9.2, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (7, 1, DATE '2026-10-07', 8.4, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (8, 1, DATE '2026-10-08', 7.2, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (9, 1, DATE '2026-10-09', 6.8, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (10, 1, DATE '2026-10-10', 7.4, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (11, 1, DATE '2026-10-11', 8.1, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (12, 1, DATE '2026-10-12', 7.0, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (13, 1, DATE '2026-10-13', 9.5, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (14, 1, DATE '2026-10-14', 8.7, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (15, 2, DATE '2026-10-01', 4.1, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (16, 2, DATE '2026-10-02', 4.5, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (17, 2, DATE '2026-10-03', 4.0, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (18, 2, DATE '2026-10-04', 5.2, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (19, 2, DATE '2026-10-05', 4.8, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (20, 2, DATE '2026-10-06', 6.1, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (21, 2, DATE '2026-10-07', 5.7, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (22, 2, DATE '2026-10-08', 4.4, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (23, 2, DATE '2026-10-09', 4.9, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (24, 2, DATE '2026-10-10', 5.3, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (25, 3, DATE '2026-10-01', 10.2, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (26, 3, DATE '2026-10-02', 9.8, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (27, 3, DATE '2026-10-03', 11.0, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (28, 3, DATE '2026-10-04', 10.6, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (29, 3, DATE '2026-10-05', 12.1, 'SIMULADO');

INSERT INTO tb_consumo_diario (cd_consumo, cd_residencia, dt_consumo, vl_consumo_kwh, ds_origem)
VALUES (30, 3, DATE '2026-10-06', 11.5, 'SIMULADO');

INSERT INTO tb_meta_consumo (cd_meta, cd_residencia, dt_inicio, dt_fim, vl_limite_kwh)
VALUES (1, 1, DATE '2026-10-01', DATE '2026-10-31', 180);

INSERT INTO tb_meta_consumo (cd_meta, cd_residencia, dt_inicio, dt_fim, vl_limite_kwh)
VALUES (2, 1, DATE '2026-11-01', DATE '2026-11-30', 175);

INSERT INTO tb_meta_consumo (cd_meta, cd_residencia, dt_inicio, dt_fim, vl_limite_kwh)
VALUES (3, 2, DATE '2026-10-01', DATE '2026-10-31', 140);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (1, 1, 1, 1, 0.5);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (2, 1, 2, 1, 4);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (3, 1, 4, 6, 5);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (4, 2, 1, 1, 0.4);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (5, 2, 3, 1, 5);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (6, 2, 4, 4, 4);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (7, 3, 2, 2, 5);

INSERT INTO tb_residencia_aparelho (cd_vinculo, cd_residencia, cd_aparelho, qt_aparelhos, vl_horas_dia)
VALUES (8, 3, 5, 2, 8);

INSERT INTO tb_previsao_consumo (cd_previsao, cd_residencia, dt_referencia, dt_alvo, vl_previsto_kwh, ds_modelo, dt_geracao, ds_origem)
VALUES (1, 1, DATE '2026-10-07', DATE '2026-10-08', 7.2, 'FIXTURE_NAO_TREINADO', DATE '2026-11-07', 'SIMULADO');

INSERT INTO tb_analise_padrao (cd_analise, cd_residencia, dt_inicio, dt_fim, cd_grupo, ds_interpretacao, ds_modelo, ds_origem)
VALUES (1, 1, DATE '2026-10-01', DATE '2026-10-07', 0, 'Exemplo sintetico para testar persistencia; nao e analise de IA.', 'FIXTURE_NAO_TREINADO', 'SIMULADO');

INSERT INTO tb_previsao_consumo (cd_previsao, cd_residencia, dt_referencia, dt_alvo, vl_previsto_kwh, ds_modelo, dt_geracao, ds_origem)
VALUES (2, 1, DATE '2026-10-08', DATE '2026-10-09', 7.4, 'FIXTURE_NAO_TREINADO', DATE '2026-11-07', 'SIMULADO');

INSERT INTO tb_analise_padrao (cd_analise, cd_residencia, dt_inicio, dt_fim, cd_grupo, ds_interpretacao, ds_modelo, ds_origem)
VALUES (2, 1, DATE '2026-10-02', DATE '2026-10-08', 1, 'Exemplo sintetico para testar persistencia; nao e analise de IA.', 'FIXTURE_NAO_TREINADO', 'SIMULADO');

INSERT INTO tb_previsao_consumo (cd_previsao, cd_residencia, dt_referencia, dt_alvo, vl_previsto_kwh, ds_modelo, dt_geracao, ds_origem)
VALUES (3, 1, DATE '2026-10-09', DATE '2026-10-10', 7.6, 'FIXTURE_NAO_TREINADO', DATE '2026-11-07', 'SIMULADO');

INSERT INTO tb_analise_padrao (cd_analise, cd_residencia, dt_inicio, dt_fim, cd_grupo, ds_interpretacao, ds_modelo, ds_origem)
VALUES (3, 1, DATE '2026-10-03', DATE '2026-10-09', 2, 'Exemplo sintetico para testar persistencia; nao e analise de IA.', 'FIXTURE_NAO_TREINADO', 'SIMULADO');

INSERT INTO tb_previsao_consumo (cd_previsao, cd_residencia, dt_referencia, dt_alvo, vl_previsto_kwh, ds_modelo, dt_geracao, ds_origem)
VALUES (4, 1, DATE '2026-10-10', DATE '2026-10-11', 7.8, 'FIXTURE_NAO_TREINADO', DATE '2026-11-07', 'SIMULADO');

INSERT INTO tb_analise_padrao (cd_analise, cd_residencia, dt_inicio, dt_fim, cd_grupo, ds_interpretacao, ds_modelo, ds_origem)
VALUES (4, 1, DATE '2026-10-04', DATE '2026-10-10', 0, 'Exemplo sintetico para testar persistencia; nao e analise de IA.', 'FIXTURE_NAO_TREINADO', 'SIMULADO');

INSERT INTO tb_previsao_consumo (cd_previsao, cd_residencia, dt_referencia, dt_alvo, vl_previsto_kwh, ds_modelo, dt_geracao, ds_origem)
VALUES (5, 1, DATE '2026-10-11', DATE '2026-10-12', 8.0, 'FIXTURE_NAO_TREINADO', DATE '2026-11-07', 'SIMULADO');

INSERT INTO tb_analise_padrao (cd_analise, cd_residencia, dt_inicio, dt_fim, cd_grupo, ds_interpretacao, ds_modelo, ds_origem)
VALUES (5, 1, DATE '2026-10-05', DATE '2026-10-11', 1, 'Exemplo sintetico para testar persistencia; nao e analise de IA.', 'FIXTURE_NAO_TREINADO', 'SIMULADO');

COMMIT;

