package sv.edu.ufg.calculodesalario.repositorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PrestacionRepositorio {

    private final JdbcTemplate jdbc;

    public PrestacionRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Dias de aguinaldo segun antiguedad. Vacio si aun no cumple un anio. */
    public Optional<Integer> buscarDiasAguinaldo(int anios, LocalDate fecha) {
        String sql = """
                SELECT dias_salario
                FROM regla_aguinaldo
                WHERE ? >= anios_min
                  AND (anios_max IS NULL OR ? < anios_max)
                  AND vigencia_desde <= ?
                  AND (vigencia_hasta IS NULL OR vigencia_hasta >= ?)
                ORDER BY anios_min DESC
                LIMIT 1
                """;
        List<Integer> filas = jdbc.queryForList(sql, Integer.class, anios, anios, fecha, fecha);
        return filas.isEmpty() ? Optional.empty() : Optional.of(filas.get(0));
    }

    public BigDecimal buscarParametro(String clave) {
        String sql = "SELECT valor FROM parametro_general WHERE clave = ?";
        return jdbc.queryForObject(sql, BigDecimal.class, clave);
    }

    public BigDecimal buscarFactorRecargo(String codigo) {
        String sql = "SELECT factor FROM recargo_jornada WHERE codigo = ?";
        return jdbc.queryForObject(sql, BigDecimal.class, codigo);
    }

    public String buscarDescripcionRecargo(String codigo) {
        String sql = "SELECT descripcion FROM recargo_jornada WHERE codigo = ?";
        return jdbc.queryForObject(sql, String.class, codigo);
    }
}
