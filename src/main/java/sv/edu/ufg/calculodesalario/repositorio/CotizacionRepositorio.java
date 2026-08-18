package sv.edu.ufg.calculodesalario.repositorio;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sv.edu.ufg.calculodesalario.dominio.ParametroCotizacion;

@Repository
public class CotizacionRepositorio {

    private static final RowMapper<ParametroCotizacion> MAPEADOR = (rs, n) -> new ParametroCotizacion(
            rs.getString("codigo"),
            rs.getString("descripcion"),
            rs.getBigDecimal("porcentaje"),
            rs.getBigDecimal("base_maxima"));

    private final JdbcTemplate jdbc;

    public CotizacionRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public ParametroCotizacion buscarPorCodigo(String codigo, LocalDate fecha) {
        String sql = """
                SELECT codigo, descripcion, porcentaje, base_maxima
                FROM parametro_cotizacion
                WHERE codigo = ?
                  AND vigencia_desde <= ?
                  AND (vigencia_hasta IS NULL OR vigencia_hasta >= ?)
                ORDER BY vigencia_desde DESC
                LIMIT 1
                """;

        return jdbc.queryForObject(sql, MAPEADOR, codigo, fecha, fecha);
    }
}
