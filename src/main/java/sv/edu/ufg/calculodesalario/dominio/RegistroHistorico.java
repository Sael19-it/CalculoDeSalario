package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Un calculo guardado en la base de datos, tal como se leyo de ella. */
public record RegistroHistorico(
        long id,
        BigDecimal salarioBruto,
        String periodicidad,
        BigDecimal descuentoAfp,
        BigDecimal descuentoIsss,
        BigDecimal descuentoDeRenta,
        BigDecimal salarioLiquido,
        LocalDateTime calculadoEn) {
}
