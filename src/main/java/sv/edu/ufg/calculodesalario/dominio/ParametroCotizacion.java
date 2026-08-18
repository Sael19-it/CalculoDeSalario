package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ParametroCotizacion(
        String codigo,
        String descripcion,
        BigDecimal porcentaje,
        BigDecimal baseMaxima) {

    public BigDecimal calcular(BigDecimal salario, Periodicidad periodicidad) {
        BigDecimal base = salario;

        if (baseMaxima != null) {
            BigDecimal topeAjustado = baseMaxima.divide(
                    periodicidad.getDivisorDesdeMensual(), 2, RoundingMode.HALF_UP);
            if (salario.compareTo(topeAjustado) > 0) {
                base = topeAjustado;
            }
        }

        return base.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }
}
