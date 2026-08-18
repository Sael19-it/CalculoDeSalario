-- ============================================================
-- Tabla ISR MENSUAL (Decreto Ejecutivo No. 10, 30-abr-2025)
-- ============================================================
INSERT INTO tramo_isr (periodicidad, desde, hasta, cuota_fija, porcentaje, sobre_exceso, vigencia_desde) VALUES
('MENSUAL',    0.00,  550.00,   0.00, 0.00000,    0.00, '2025-05-01'),
('MENSUAL',  550.01,  895.24,  17.67, 0.10000,  550.00, '2025-05-01'),
('MENSUAL',  895.25, 2038.10,  60.00, 0.20000,  895.24, '2025-05-01'),
('MENSUAL', 2038.11,    NULL, 288.57, 0.30000, 2038.10, '2025-05-01');

-- Tabla ISR QUINCENAL
INSERT INTO tramo_isr (periodicidad, desde, hasta, cuota_fija, porcentaje, sobre_exceso, vigencia_desde) VALUES
('QUINCENAL',    0.00,  275.00,   0.00, 0.00000,    0.00, '2025-05-01'),
('QUINCENAL',  275.01,  447.62,   8.83, 0.10000,  275.00, '2025-05-01'),
('QUINCENAL',  447.63, 1019.05,  30.00, 0.20000,  447.62, '2025-05-01'),
('QUINCENAL', 1019.06,    NULL, 144.28, 0.30000, 1019.05, '2025-05-01');

-- Tabla ISR SEMANAL
INSERT INTO tramo_isr (periodicidad, desde, hasta, cuota_fija, porcentaje, sobre_exceso, vigencia_desde) VALUES
('SEMANAL',   0.00, 137.50,  0.00, 0.00000,   0.00, '2025-05-01'),
('SEMANAL', 137.51, 223.81,  4.42, 0.10000, 137.50, '2025-05-01'),
('SEMANAL', 223.82, 509.52, 15.00, 0.20000, 223.81, '2025-05-01'),
('SEMANAL', 509.53,   NULL, 72.14, 0.30000, 509.52, '2025-05-01');

-- ============================================================
-- Cotizaciones ISSS y AFP
-- base_maxima NULL = sin tope
-- ============================================================
INSERT INTO parametro_cotizacion (codigo, descripcion, porcentaje, base_maxima, vigencia_desde) VALUES
('ISSS_LAB', 'Cotizacion ISSS a cargo del trabajador', 0.03000, 1000.00, '2025-01-01'),
('ISSS_PAT', 'Cotizacion ISSS a cargo del patrono',    0.07500, 1000.00, '2025-01-01'),
('AFP_LAB',  'Cotizacion AFP a cargo del trabajador',  0.07250,    NULL, '2023-01-01'),
('AFP_PAT',  'Cotizacion AFP a cargo del patrono',     0.08750,    NULL, '2023-01-01');

-- ============================================================
-- Aguinaldo (Art. 198 Codigo de Trabajo)
-- anios_max NULL = sin limite superior
-- ============================================================
INSERT INTO regla_aguinaldo (anios_min, anios_max, dias_salario, vigencia_desde) VALUES
( 1,    3, 15, '2000-01-01'),
( 3,    9, 19, '2000-01-01'),
(10, NULL, 21, '2000-01-01');

-- ============================================================
-- Recargos de jornada
-- ============================================================
INSERT INTO recargo_jornada (codigo, descripcion, factor, base_legal) VALUES
('EXTRA_DIURNA',   'Hora extra diurna: 100% de recargo',            2.000, 'Art. 169 CT'),
('EXTRA_NOCTURNA', 'Hora extra nocturna: doble mas 25% nocturnidad', 2.500, 'Art. 168-169 CT'),
('NOCTURNA_ORD',   'Hora ordinaria nocturna: 25% de recargo',        1.250, 'Art. 168 CT'),
('ASUETO',         'Trabajo en dia de asueto: pago doble',           2.000, 'Art. 192 CT'),
('SEPTIMO_DIA',    'Trabajo en dia de descanso semanal',             2.000, 'Art. 175 CT');

-- ============================================================
-- Parametros generales
-- ============================================================
INSERT INTO parametro_general (clave, valor, descripcion, base_legal) VALUES
('BONO_VACACIONES_PCT',   0.3000, 'Recargo sobre salario de vacacion',       'Art. 177 CT'),
('DIAS_VACACION',        15.0000, 'Dias de vacacion anual remunerada',       'Art. 177 CT'),
('DIAS_MES_COMERCIAL',   30.0000, 'Dias usados para obtener salario diario', 'Convencion'),
('Q25_PORCENTAJE',        0.5000, 'Porcentaje del salario mensual',          'Quincena 25'),
('Q25_SALARIO_TOPE',   1500.0000, 'Salario mensual maximo para aplicar',     'Quincena 25');
