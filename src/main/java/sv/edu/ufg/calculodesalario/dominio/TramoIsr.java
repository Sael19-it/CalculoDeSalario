package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TramoIsr(
        BigDecimal desde,
        BigDecimal hasta,
        BigDecimal cuotaFija,
        BigDecimal porcentaje,
        BigDecimal sobreExceso) {

    public BigDecimal calcularImpuesto(BigDecimal rentaImponible) {
        BigDecimal exceso = rentaImponible.subtract(sobreExceso);
        if (exceso.compareTo(BigDecimal.ZERO) < 0) {
            exceso = BigDecimal.ZERO;
        }
        return cuotaFija
                .add(exceso.multiply(porcentaje))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
