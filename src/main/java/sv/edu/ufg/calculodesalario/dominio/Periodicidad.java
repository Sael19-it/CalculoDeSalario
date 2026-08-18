package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

public enum Periodicidad {
    MENSUAL(new BigDecimal("1")),
    QUINCENAL(new BigDecimal("2")),
    SEMANAL(new BigDecimal("4.33333"));

    private final BigDecimal divisorDesdeMensual;

    Periodicidad(BigDecimal divisorDesdeMensual) {
        this.divisorDesdeMensual = divisorDesdeMensual;
    }

    public BigDecimal getDivisorDesdeMensual() {
        return divisorDesdeMensual;
    }
}
