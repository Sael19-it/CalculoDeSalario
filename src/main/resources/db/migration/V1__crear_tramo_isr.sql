CREATE TABLE tramo_isr (
    id              SERIAL PRIMARY KEY,
    periodicidad    VARCHAR(10)   NOT NULL,
    desde           NUMERIC(12,2) NOT NULL,
    hasta           NUMERIC(12,2),
    cuota_fija      NUMERIC(12,2) NOT NULL,
    porcentaje      NUMERIC(7,5)  NOT NULL,
    sobre_exceso    NUMERIC(12,2) NOT NULL,
    vigencia_desde  DATE          NOT NULL,
    vigencia_hasta  DATE,
    CONSTRAINT ck_tramo_periodicidad
        CHECK (periodicidad IN ('MENSUAL','QUINCENAL','SEMANAL')),
    CONSTRAINT ck_tramo_rango
        CHECK (hasta IS NULL OR hasta >= desde)
);
