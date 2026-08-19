package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

/**
 * Cada cuanto se paga el salario.
 *
 * Cada opcion guarda cuantos periodos de ese tipo caben en un mes. Ese dato
 * se necesita porque varios limites legales estan definidos por mes: el techo
 * de $1,000 del ISSS, por ejemplo. Al pagar quincenalmente ese techo debe
 * dividirse entre 2.
 *
 * Guardar el numero aqui evita repetir condicionales por todo el proyecto:
 * cada periodicidad carga su propio divisor. Ademas, si algun dia se agrega
 * una nueva forma de pago, el compilador obliga a definir su valor.
 */
public enum Periodicidad {

    MENSUAL(new BigDecimal("1")),
    QUINCENAL(new BigDecimal("2")),
    SEMANAL(new BigDecimal("4.33333"));   // 52 semanas / 12 meses

    private final BigDecimal periodosPorMes;

    Periodicidad(BigDecimal periodosPorMes) {
        this.periodosPorMes = periodosPorMes;
    }

    public BigDecimal getPeriodosPorMes() {
        return periodosPorMes;
    }
}
