# Calculadora de Salario — El Salvador

Aplicación web que calcula el salario neto de un trabajador salvadoreño aplicando
los descuentos de ley vigentes, además de las prestaciones laborales más comunes.

**Universidad Francisco Gavidia** · Ingeniería en Sistemas y Ciberseguridad

---

## Funcionalidades

| Módulo | Descripción |
|---|---|
| Salario neto | Descuentos de AFP, ISSS e ISR — mensual, quincenal y semanal |
| Aguinaldo | Según antigüedad, con cálculo proporcional (Art. 198 CT) |
| Vacaciones | 15 días de salario más el recargo del 30% (Art. 177 CT) |
| Horas extra | Cinco tipos de jornada con sus recargos legales |
| Quincena 25 | Verificación de requisitos y monto |
| Historial | Registro de cálculos realizados |

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4 |
| Vistas | Thymeleaf |
| Base de datos | PostgreSQL 16 |
| Acceso a datos | Spring JdbcTemplate |
| Migraciones | Flyway |
| Construcción | Maven |
| Estilos | Bulma CSS |
| Gráficos | Chart.js |
| Pruebas | JUnit 5 + AssertJ |

---

## Requisitos

- Java 21 o superior
- PostgreSQL 16
- Maven (incluido en el proyecto como `./mvnw`)

---

## Instalación

### 1. Crear la base de datos

```bash
sudo -u postgres psql -p 5433 -c "CREATE DATABASE calculodesalario;"
sudo -u postgres psql -p 5433 -c "CREATE USER calculodesalario_app WITH PASSWORD 'calculodesalario123';"
sudo -u postgres psql -p 5433 -c "GRANT ALL PRIVILEGES ON DATABASE calculodesalario TO calculodesalario_app;"
sudo -u postgres psql -p 5433 -d calculodesalario -c "GRANT ALL ON SCHEMA public TO calculodesalario_app;"
```

> La última línea es indispensable desde PostgreSQL 15: antes el permiso sobre el
> esquema `public` se heredaba y ahora debe otorgarse de forma explícita.

**No hay que crear las tablas a mano.** Flyway las crea y las puebla al arrancar.

### 2. Ajustar la conexión

Si tu PostgreSQL usa el puerto 5432 en lugar de 5433, editá
`src/main/resources/application.yml`.

### 3. Ejecutar

```bash
./mvnw spring-boot:run
```

Abrir <http://localhost:8080>

### 4. Ejecutar las pruebas

```bash
./mvnw test
```

Resultado esperado: `Tests run: 18, Failures: 0, Errors: 0`

---

## Decisiones de diseño

### Los valores legales viven en la base de datos, no en el código

Los tramos del ISR, los porcentajes de cotización y los días de aguinaldo cambian
por decreto — la tabla de retención cambió en abril de 2025. Cada parámetro se
almacena con campos `vigencia_desde` y `vigencia_hasta`, de modo que:

- Un cambio de ley es un `INSERT`, no una modificación del código.
- Los cálculos con fecha anterior siguen usando la tabla que estaba vigente entonces.

### El ISR se calcula sobre la renta imponible

Las cotizaciones al ISSS y a las AFP son remuneraciones no gravadas. La base
gravable es el salario bruto menos esas cotizaciones:
Calcularlo sobre el bruto es el error más frecuente en calculadoras similares.

### BigDecimal para todos los montos

`double` guarda aproximaciones binarias (`0.1 + 0.2` da `0.30000000000000004`).
Toda la cadena de cálculo usa `BigDecimal` con redondeo `HALF_UP` a dos decimales.

### JdbcTemplate en lugar de un ORM

Las tablas de parámetros son de solo lectura: se consultan, nunca se editan desde
la aplicación. El SQL explícito mantiene el código transparente y evita la
complejidad de gestionar el ciclo de vida de entidades que no cambian.

---

## Estructura
---

## Base legal

- **Decreto Ejecutivo No. 10** (30 de abril de 2025) — tablas de retención del ISR
- **Código de Trabajo** — Arts. 168, 169, 175, 177, 192, 196, 197, 198
- **Ley de Impuesto sobre la Renta** — Art. 29
- **Ley Integral del Sistema de Pensiones** — eliminación del tope de cotización (2023)

---

