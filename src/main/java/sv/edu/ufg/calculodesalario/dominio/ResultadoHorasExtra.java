package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;

public record ResultadoHorasExtra(
        BigDecimal salarioHoraOrdinaria,
        String tipoJornada,
        String descripcion,
        BigDecimal factor,
        BigDecimal horas,
        BigDecimal valorPorHora,
        BigDecimal total) {
}
