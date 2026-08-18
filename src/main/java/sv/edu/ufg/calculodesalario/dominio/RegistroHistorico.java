package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegistroHistorico(
        long id,
        BigDecimal salarioBruto,
        String periodicidad,
        BigDecimal montoAfp,
        BigDecimal montoIsss,
        BigDecimal montoIsr,
        BigDecimal salarioLiquido,
        LocalDateTime calculadoEn) {
}
