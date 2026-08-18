package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

public record ResultadoVacaciones(
        BigDecimal salarioDiario,
        int diasVacacion,
        BigDecimal salarioBase,
        BigDecimal recargo,
        BigDecimal total) {
}
