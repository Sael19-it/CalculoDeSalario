package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

public record ResultadoAguinaldo(
        BigDecimal salarioMensual,
        BigDecimal salarioDiario,
        int aniosAntiguedad,
        int diasCorrespondientes,
        boolean esProporcional,
        BigDecimal monto) {
}
