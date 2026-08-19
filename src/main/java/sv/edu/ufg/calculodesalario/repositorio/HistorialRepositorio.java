package sv.edu.ufg.calculodesalario.repositorio;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sv.edu.ufg.calculodesalario.dominio.RegistroHistorico;
import sv.edu.ufg.calculodesalario.dominio.ResultadoSalario;

/**
 * Guarda y consulta los calculos que se han hecho.
 *
 * El guardado se dispara desde el controlador y no desde la calculadora, de
 * manera deliberada: asi la calculadora solo recibe numeros y devuelve numeros,
 * sin tocar la base de datos. Eso permite probarla de forma aislada.
 */
@Repository
public class HistorialRepositorio {

    /**
     * Convierte una fila de calculo_historico en un objeto de Java.
     *
     * "filaActual" es el cursor sobre la fila que se lee en este momento y
     * "posicionDeLaFila" su numero de orden dentro del resultado.
     */
    private static final RowMapper<RegistroHistorico> TRADUCTOR_DE_FILA =
            (filaActual, posicionDeLaFila) -> new RegistroHistorico(
                    filaActual.getLong("id"),
                    filaActual.getBigDecimal("salario_bruto"),
                    filaActual.getString("periodicidad"),
                    filaActual.getBigDecimal("descuento_afp"),
                    filaActual.getBigDecimal("descuento_isss"),
                    filaActual.getBigDecimal("descuento_renta"),
                    filaActual.getBigDecimal("salario_liquido"),
                    filaActual.getTimestamp("calculado_en").toLocalDateTime());

    private final JdbcTemplate jdbc;

    public HistorialRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Registra un calculo recien hecho. */
    public void guardar(ResultadoSalario resultado, int aniosDeAntiguedad) {

        String insercion = """
                INSERT INTO calculo_historico
                    (salario_bruto, periodicidad, anios_antiguedad,
                     descuento_afp, descuento_isss, descuento_renta, salario_liquido)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbc.update(insercion,
                resultado.salarioBruto(),
                resultado.periodicidad().name(),
                aniosDeAntiguedad,
                resultado.descuentoAfp(),
                resultado.descuentoIsss(),
                resultado.descuentoDeRenta(),
                resultado.salarioLiquido());
    }

    /** Devuelve los calculos mas recientes, empezando por el ultimo. */
    public List<RegistroHistorico> listarUltimos(int cuantos) {

        String consulta = """
                SELECT id, salario_bruto, periodicidad, descuento_afp, descuento_isss,
                       descuento_renta, salario_liquido, calculado_en
                FROM calculo_historico
                ORDER BY calculado_en DESC
                LIMIT ?
                """;

        return jdbc.query(consulta, TRADUCTOR_DE_FILA, cuantos);
    }

    /** Borra todo el historial. */
    public void limpiar() {
        jdbc.update("DELETE FROM calculo_historico");
    }
}
