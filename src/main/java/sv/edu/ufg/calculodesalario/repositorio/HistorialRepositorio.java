package sv.edu.ufg.calculodesalario.repositorio;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import sv.edu.ufg.calculodesalario.dominio.RegistroHistorico;
import sv.edu.ufg.calculodesalario.dominio.ResultadoSalario;

@Repository
public class HistorialRepositorio {

    private static final RowMapper<RegistroHistorico> MAPEADOR = (rs, n) -> new RegistroHistorico(
            rs.getLong("id"),
            rs.getBigDecimal("salario_bruto"),
            rs.getString("periodicidad"),
            rs.getBigDecimal("monto_afp"),
            rs.getBigDecimal("monto_isss"),
            rs.getBigDecimal("monto_isr"),
            rs.getBigDecimal("salario_liquido"),
            rs.getTimestamp("calculado_en").toLocalDateTime());

    private final JdbcTemplate jdbc;

    public HistorialRepositorio(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void guardar(ResultadoSalario r, int aniosAntiguedad) {
        String sql = """
                INSERT INTO calculo_historico
                    (salario_bruto, periodicidad, anios_antiguedad,
                     monto_afp, monto_isss, monto_isr, salario_liquido)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbc.update(sql,
                r.salarioBruto(), r.periodicidad().name(), aniosAntiguedad,
                r.montoAfp(), r.montoIsss(), r.montoIsr(), r.salarioLiquido());
    }

    public List<RegistroHistorico> listarUltimos(int cantidad) {
        String sql = """
                SELECT id, salario_bruto, periodicidad, monto_afp, monto_isss,
                       monto_isr, salario_liquido, calculado_en
                FROM calculo_historico
                ORDER BY calculado_en DESC
                LIMIT ?
                """;
        return jdbc.query(sql, MAPEADOR, cantidad);
    }

    public void limpiar() {
        jdbc.update("DELETE FROM calculo_historico");
    }
}
