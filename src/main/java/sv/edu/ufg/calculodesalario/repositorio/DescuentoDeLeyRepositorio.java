package sv.edu.ufg.calculodesalario.repositorio;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sv.edu.ufg.calculodesalario.dominio.DescuentoDeLey;

/**
 * Lee de la base de datos los porcentajes de ISSS y AFP.
 *
 * Igual que con la tabla de renta, estos porcentajes viven en la base y no en
 * el codigo porque pueden cambiar por ley.
 */
@Repository
public class DescuentoDeLeyRepositorio {

    /**
     * Convierte una fila de la tabla descuento_de_ley en un objeto de Java.
     *
     * "filaActual" es el cursor sobre la fila que se esta leyendo y
     * "posicionDeLaFila" es su numero dentro del resultado, que no necesitamos.
     *
     * Detalle importante: cuando la columna salario_maximo esta vacia en la
     * base, getBigDecimal devuelve null en Java. Eso no es un error: es la
     * forma de representar que ese descuento no tiene techo, como ocurre con
     * las dos AFP.
     */
    private static final RowMapper<DescuentoDeLey> TRADUCTOR_DE_FILA =
            (filaActual, posicionDeLaFila) -> new DescuentoDeLey(
                    filaActual.getString("codigo"),
                    filaActual.getString("descripcion"),
                    filaActual.getBigDecimal("porcentaje"),
                    filaActual.getBigDecimal("salario_maximo"));

    private final JdbcTemplate jdbc;

    public DescuentoDeLeyRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Busca un descuento por su codigo, segun la ley vigente en la fecha dada.
     *
     * @param codigo  ISSS_TRABAJADOR, ISSS_PATRONO, AFP_TRABAJADOR o AFP_PATRONO
     */
    public DescuentoDeLey buscarPorCodigo(String codigo, LocalDate fecha) {

        String consulta = """
                SELECT codigo, descripcion, porcentaje, salario_maximo
                FROM descuento_de_ley
                WHERE codigo = ?
                  AND vigencia_desde <= ?
                  AND (vigencia_hasta IS NULL OR vigencia_hasta >= ?)
                ORDER BY vigencia_desde DESC
                LIMIT 1
                """;

        return jdbc.queryForObject(consulta, TRADUCTOR_DE_FILA, codigo, fecha, fecha);
    }
}
