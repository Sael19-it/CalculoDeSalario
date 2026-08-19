package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Un descuento obligatorio de planilla: ISSS o AFP, sea del trabajador
 * o del patrono.
 *
 * Los cuatro codigos que existen en la base de datos son:
 *
 *     ISSS_TRABAJADOR   3.00%  con salario maximo de $1,000
 *     ISSS_PATRONO      7.50%  con salario maximo de $1,000
 *     AFP_TRABAJADOR    7.25%  sin salario maximo
 *     AFP_PATRONO       8.75%  sin salario maximo
 *
 * El ISSS tiene techo: el porcentaje se aplica unicamente hasta $1,000, asi
 * que el descuento del trabajador nunca pasa de $30 al mes por muy alto que
 * sea su salario. La AFP no tiene techo desde que la Ley Integral del Sistema
 * de Pensiones lo elimino en enero de 2023.
 */
public record DescuentoDeLey(
        String codigo,
        String descripcion,
        BigDecimal porcentaje,
        BigDecimal salarioMaximo) {

    /**
     * Calcula el monto a descontar de un salario.
     *
     * @param salario       el salario del periodo que se esta calculando
     * @param periodicidad  sirve para ajustar el techo: los $1,000 del ISSS
     *                      estan definidos por mes, asi que en quincena el
     *                      techo baja a $500 y en semana a unos $231
     */
    public BigDecimal calcularDescuento(BigDecimal salario, Periodicidad periodicidad) {

        BigDecimal salarioSobreElQueSeCalcula = salario;

        // Un salarioMaximo nulo significa "este descuento no tiene techo".
        // Es el caso de las dos AFP y es intencional: si manana la ley vuelve
        // a poner un tope, basta con actualizar esa columna en la base de datos.
        if (salarioMaximo != null) {

            BigDecimal techoDelPeriodo = salarioMaximo.divide(
                    periodicidad.getPeriodosPorMes(), 2, RoundingMode.HALF_UP);

            if (salario.compareTo(techoDelPeriodo) > 0) {
                salarioSobreElQueSeCalcula = techoDelPeriodo;
            }
        }

        return salarioSobreElQueSeCalcula
                .multiply(porcentaje)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
