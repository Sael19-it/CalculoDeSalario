-- ============================================================
-- Renombra tablas y columnas al vocabulario que usa el negocio
-- en El Salvador. Se evita la sigla ISR y la palabra "tramo"
-- porque exigen conocer la jerga de Hacienda para entenderlas.
--
-- No se modifican las migraciones anteriores: Flyway guarda un
-- checksum de cada archivo ya aplicado y fallaria al arrancar.
-- Los cambios se hacen siempre hacia adelante.
-- ============================================================

-- ---- Rangos de la tabla de renta ----
ALTER TABLE tramo_isr RENAME TO rango_de_renta;

ALTER TABLE rango_de_renta RENAME COLUMN desde        TO salario_desde;
ALTER TABLE rango_de_renta RENAME COLUMN hasta        TO salario_hasta;
ALTER TABLE rango_de_renta RENAME COLUMN cuota_fija   TO descuento_base;
ALTER TABLE rango_de_renta RENAME COLUMN sobre_exceso TO excedente_desde;

-- ---- Descuentos de ley (ISSS y AFP) ----
ALTER TABLE parametro_cotizacion RENAME TO descuento_de_ley;

ALTER TABLE descuento_de_ley RENAME COLUMN base_maxima TO salario_maximo;

-- Los codigos pasan de LAB/PAT a palabras completas, mas legibles.
-- Hay que quitar primero la restriccion, porque los valores nuevos
-- no estarian permitidos por la regla anterior.
ALTER TABLE descuento_de_ley DROP CONSTRAINT IF EXISTS ck_cotizacion_codigo;

UPDATE descuento_de_ley SET codigo = 'ISSS_TRABAJADOR' WHERE codigo = 'ISSS_LAB';
UPDATE descuento_de_ley SET codigo = 'ISSS_PATRONO'    WHERE codigo = 'ISSS_PAT';
UPDATE descuento_de_ley SET codigo = 'AFP_TRABAJADOR'  WHERE codigo = 'AFP_LAB';
UPDATE descuento_de_ley SET codigo = 'AFP_PATRONO'     WHERE codigo = 'AFP_PAT';

ALTER TABLE descuento_de_ley ADD CONSTRAINT ck_descuento_de_ley_codigo
    CHECK (codigo IN ('ISSS_TRABAJADOR','ISSS_PATRONO',
                      'AFP_TRABAJADOR','AFP_PATRONO'));

-- ---- Historial de calculos ----
ALTER TABLE calculo_historico RENAME COLUMN monto_afp  TO descuento_afp;
ALTER TABLE calculo_historico RENAME COLUMN monto_isss TO descuento_isss;
ALTER TABLE calculo_historico RENAME COLUMN monto_isr  TO descuento_renta;

-- Nota: no se renombran indices ni restricciones CHECK de rango_de_renta.
-- Sus nombres internos no afectan el funcionamiento y renombrarlos solo
-- agregaria puntos de falla.
