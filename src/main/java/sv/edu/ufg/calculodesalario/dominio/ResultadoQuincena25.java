package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

public record ResultadoQuincena25(
        boolean aplica,
        String motivo,
        BigDecimal salarioMensual,
        BigDecimal monto) {
}
