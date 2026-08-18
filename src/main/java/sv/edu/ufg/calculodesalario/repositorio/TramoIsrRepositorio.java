package sv.edu.ufg.calculodesalario.repositorio;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sv.edu.ufg.calculodesalario.dominio.Periodicidad;
import sv.edu.ufg.calculodesalario.dominio.TramoIsr;

@Repository
public class TramoIsrRepositorio {

    private static final RowMapper<TramoIsr> MAPEADOR = (rs, n) -> new TramoIsr(
            rs.getBigDecimal("desde"),
            rs.getBigDecimal("hasta"),
            rs.getBigDecimal("cuota_fija"),
            rs.getBigDecimal("porcentaje"),
            rs.getBigDecimal("sobre_exceso"));

    private final JdbcTemplate jdbc;

    public TramoIsrRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TramoIsr buscarTramo(Periodicidad periodicidad, BigDecimal rentaImponible, LocalDate fecha) {
        String sql = """
                SELECT desde, hasta, cuota_fija, porcentaje, sobre_exceso
                FROM tramo_isr
                WHERE periodicidad = ?
                  AND ? >= desde
                  AND (hasta IS NULL OR ? <= hasta)
                  AND vigencia_desde <= ?
                  AND (vigencia_hasta IS NULL OR vigencia_hasta >= ?)
                ORDER BY vigencia_desde DESC
                LIMIT 1
                """;

        return jdbc.queryForObject(sql, MAPEADOR,
                periodicidad.name(), rentaImponible, rentaImponible, fecha, fecha);
    }
}
