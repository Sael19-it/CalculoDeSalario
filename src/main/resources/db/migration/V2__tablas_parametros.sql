-- Cotizaciones: ISSS y AFP, laboral y patronal
CREATE TABLE parametro_cotizacion (
    id              SERIAL PRIMARY KEY,
    codigo          VARCHAR(20)   NOT NULL,
    descripcion     VARCHAR(120)  NOT NULL,
    porcentaje      NUMERIC(7,5)  NOT NULL,
    base_maxima     NUMERIC(12,2),
    vigencia_desde  DATE          NOT NULL,
    vigencia_hasta  DATE,
    CONSTRAINT ck_cotizacion_codigo
        CHECK (codigo IN ('ISSS_LAB','ISSS_PAT','AFP_LAB','AFP_PAT'))
);

-- Aguinaldo: dias de salario segun antiguedad (Art. 198 CT)
CREATE TABLE regla_aguinaldo (
    id              SERIAL PRIMARY KEY,
    anios_min       INT           NOT NULL,
    anios_max       INT,
    dias_salario    INT           NOT NULL,
    vigencia_desde  DATE          NOT NULL,
    vigencia_hasta  DATE
);

-- Recargos de jornada extraordinaria y asuetos
CREATE TABLE recargo_jornada (
    id              SERIAL PRIMARY KEY,
    codigo          VARCHAR(30)   NOT NULL UNIQUE,
    descripcion     VARCHAR(160)  NOT NULL,
    factor          NUMERIC(6,3)  NOT NULL,
    base_legal      VARCHAR(60)
);

-- Parametros generales (vacaciones, Quincena 25, etc.)
CREATE TABLE parametro_general (
    clave           VARCHAR(50)   PRIMARY KEY,
    valor           NUMERIC(12,4) NOT NULL,
    descripcion     VARCHAR(200)  NOT NULL,
    base_legal      VARCHAR(80)
);

-- Historial de calculos realizados
CREATE TABLE calculo_historico (
    id                  BIGSERIAL PRIMARY KEY,
    salario_bruto       NUMERIC(12,2) NOT NULL,
    periodicidad        VARCHAR(10)   NOT NULL,
    anios_antiguedad    INT           NOT NULL,
    monto_afp           NUMERIC(12,2) NOT NULL,
    monto_isss          NUMERIC(12,2) NOT NULL,
    monto_isr           NUMERIC(12,2) NOT NULL,
    salario_liquido     NUMERIC(12,2) NOT NULL,
    calculado_en        TIMESTAMP     NOT NULL DEFAULT NOW()
);
