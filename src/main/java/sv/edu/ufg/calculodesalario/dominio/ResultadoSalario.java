package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

public record ResultadoSalario(
        BigDecimal salarioBruto,
        Periodicidad periodicidad,
        BigDecimal montoAfp,
        BigDecimal montoIsss,
        BigDecimal rentaImponible,
        BigDecimal montoIsr,
        BigDecimal totalDescuentos,
        BigDecimal salarioLiquido,
        BigDecimal afpPatronal,
        BigDecimal isssPatronal,
        BigDecimal costoTotalPatrono) {
}
