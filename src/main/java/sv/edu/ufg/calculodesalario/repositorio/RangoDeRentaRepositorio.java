package sv.edu.ufg.calculodesalario.repositorio;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sv.edu.ufg.calculodesalario.dominio.Periodicidad;
import sv.edu.ufg.calculodesalario.dominio.RangoDeRenta;

/**
 * Lee de la base de datos los rangos de la tabla de retencion de renta.
 *
 * Estos valores no estan escritos en el codigo a proposito: cambian por
 * decreto y de hecho cambiaron en abril de 2025. Al guardarlos en la base
 * de datos junto con sus fechas de vigencia, una reforma de ley se resuelve
 * insertando filas nuevas, sin tocar ni recompilar la aplicacion.
 */
@Repository
public class RangoDeRentaRepositorio {

    /**
     * Convierte una fila de la tabla rango_de_renta en un objeto de Java.
     *
     * Esta funcion no se llama a mano. JdbcTemplate la ejecuta una vez por
     * cada fila que devuelve la consulta.
     *
     * El primer parametro es un cursor posicionado sobre la fila que se esta
     * leyendo en ese momento; de ahi el nombre "filaActual". El segundo es la
     * posicion de esa fila dentro del resultado (0, 1, 2...); no lo usamos,
     * pero la interfaz de Spring obliga a recibirlo.
     *
     * Esta traduccion es justo lo que un ORM como Hibernate haria de manera
     * automatica. Escribirla a mano permite ver con exactitud que columna de
     * la base alimenta que campo del objeto.
     */
    private static final RowMapper<RangoDeRenta> TRADUCTOR_DE_FILA =
            (filaActual, posicionDeLaFila) -> new RangoDeRenta(
                    filaActual.getBigDecimal("salario_desde"),
                    filaActual.getBigDecimal("salario_hasta"),
                    filaActual.getBigDecimal("descuento_base"),
                    filaActual.getBigDecimal("porcentaje"),
                    filaActual.getBigDecimal("excedente_desde"));

    private final JdbcTemplate jdbc;

    public RangoDeRentaRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Busca en que rango de la tabla cae un salario, segun la ley vigente
     * en la fecha indicada.
     *
     * @param periodicidad         mensual, quincenal o semanal; cada una tiene
     *                             su propia tabla oficial publicada por Hacienda
     * @param salarioAfectoARenta  el salario bruto ya sin ISSS ni AFP
     * @param fecha                que version de la ley aplicar
     */
    public RangoDeRenta buscarRangoQueAplica(Periodicidad periodicidad,
                                             BigDecimal salarioAfectoARenta,
                                             LocalDate fecha) {

        // Las dos condiciones de vigencia son lo que le da sentido a guardar
        // esto en base de datos: un calculo con fecha del ano pasado usa la
        // tabla que estaba vigente entonces, no la de hoy.
        String consulta = """
                SELECT salario_desde, salario_hasta, descuento_base,
                       porcentaje, excedente_desde
                FROM rango_de_renta
                WHERE periodicidad = ?
                  AND ? >= salario_desde
                  AND (salario_hasta IS NULL OR ? <= salario_hasta)
                  AND vigencia_desde <= ?
                  AND (vigencia_hasta IS NULL OR vigencia_hasta >= ?)
                ORDER BY vigencia_desde DESC
                LIMIT 1
                """;

        // Los signos de interrogacion son espacios reservados. Los valores
        // viajan por separado y el driver los envia como datos, nunca como
        // instrucciones. Eso es lo que impide la inyeccion de SQL: aunque
        // alguien escribiera codigo malicioso en el formulario, llegaria a la
        // base como un texto cualquiera.
        return jdbc.queryForObject(consulta, TRADUCTOR_DE_FILA,
                periodicidad.name(),
                salarioAfectoARenta,
                salarioAfectoARenta,
                fecha,
                fecha);
    }
}
