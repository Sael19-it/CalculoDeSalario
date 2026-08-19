package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

/**
 * Todo lo que produce el calculo de un salario.
 *
 * Se separa en dos bloques: lo que se le descuenta al trabajador de su salario,
 * y lo que el patrono aporta ademas del salario. Son cosas distintas y
 * mezclarlas es un error frecuente: el aporte patronal no sale del bolsillo
 * del trabajador, se suma al costo de la empresa.
 */
public record ResultadoSalario(

        // --- lo que gana y lo que se le descuenta al trabajador ---
        BigDecimal salarioBruto,
        Periodicidad periodicidad,
        BigDecimal descuentoAfp,
        BigDecimal descuentoIsss,
        BigDecimal salarioAfectoARenta,
        BigDecimal descuentoDeRenta,
        BigDecimal totalDeDescuentos,
        BigDecimal salarioLiquido,

        // --- lo que el patrono paga ademas del salario ---
        BigDecimal aporteAfpDelPatrono,
        BigDecimal aporteIsssDelPatrono,
        BigDecimal costoTotalParaElPatrono) {
}
