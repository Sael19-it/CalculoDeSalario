package sv.edu.ufg.calculodesalario.servicio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sv.edu.ufg.calculodesalario.dominio.ResultadoAguinaldo;
import sv.edu.ufg.calculodesalario.dominio.ResultadoHorasExtra;
import sv.edu.ufg.calculodesalario.dominio.ResultadoQuincena25;
import sv.edu.ufg.calculodesalario.dominio.ResultadoVacaciones;
import sv.edu.ufg.calculodesalario.repositorio.PrestacionRepositorio;

@Service
public class CalculadoraPrestaciones {

    private static final BigDecimal HORAS_JORNADA = new BigDecimal("8");
    private static final BigDecimal DIAS_ANIO = new BigDecimal("365");

    private final PrestacionRepositorio repo;

    public CalculadoraPrestaciones(PrestacionRepositorio repo) {
        this.repo = repo;
    }

    private BigDecimal salarioDiario(BigDecimal salarioMensual) {
        BigDecimal diasMes = repo.buscarParametro("DIAS_MES_COMERCIAL");
        return salarioMensual.divide(diasMes, 4, RoundingMode.HALF_UP);
    }

    // ---------- Aguinaldo (Art. 196-198 CT) ----------
    public ResultadoAguinaldo calcularAguinaldo(BigDecimal salarioMensual, int anios, int diasTrabajados) {
        BigDecimal diario = salarioDiario(salarioMensual);
        Optional<Integer> dias = repo.buscarDiasAguinaldo(anios, LocalDate.now());

        if (dias.isPresent()) {
            BigDecimal monto = diario.multiply(new BigDecimal(dias.get()))
                    .setScale(2, RoundingMode.HALF_UP);
            return new ResultadoAguinaldo(salarioMensual, diario, anios, dias.get(), false, monto);
        }

        // Menos de un anio: proporcional sobre la base minima de 15 dias
        BigDecimal base = diario.multiply(new BigDecimal("15"));
        BigDecimal monto = base.multiply(new BigDecimal(diasTrabajados))
                .divide(DIAS_ANIO, 2, RoundingMode.HALF_UP);
        return new ResultadoAguinaldo(salarioMensual, diario, anios, 15, true, monto);
    }

    // ---------- Vacaciones (Art. 177 CT) ----------
    public ResultadoVacaciones calcularVacaciones(BigDecimal salarioMensual) {
        BigDecimal diario = salarioDiario(salarioMensual);
        BigDecimal dias = repo.buscarParametro("DIAS_VACACION");
        BigDecimal pctRecargo = repo.buscarParametro("BONO_VACACIONES_PCT");

        BigDecimal base = diario.multiply(dias).setScale(2, RoundingMode.HALF_UP);
        BigDecimal recargo = base.multiply(pctRecargo).setScale(2, RoundingMode.HALF_UP);

        return new ResultadoVacaciones(diario, dias.intValue(), base, recargo, base.add(recargo));
    }

    // ---------- Horas extra (Art. 168-169 CT) ----------
    public ResultadoHorasExtra calcularHorasExtra(BigDecimal salarioMensual, BigDecimal horas, String codigo) {
        BigDecimal diario = salarioDiario(salarioMensual);
        BigDecimal hora = diario.divide(HORAS_JORNADA, 4, RoundingMode.HALF_UP);

        BigDecimal factor = repo.buscarFactorRecargo(codigo);
        String descripcion = repo.buscarDescripcionRecargo(codigo);

        BigDecimal valorHora = hora.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = valorHora.multiply(horas).setScale(2, RoundingMode.HALF_UP);

        return new ResultadoHorasExtra(
                hora.setScale(2, RoundingMode.HALF_UP),
                codigo, descripcion, factor, horas, valorHora, total);
    }

    // ---------- Quincena 25 ----------
    public ResultadoQuincena25 calcularQuincena25(BigDecimal salarioMensual, int anios) {
        BigDecimal tope = repo.buscarParametro("Q25_SALARIO_TOPE");
        BigDecimal pct = repo.buscarParametro("Q25_PORCENTAJE");

        if (salarioMensual.compareTo(tope) > 0) {
            return new ResultadoQuincena25(false,
                    "El salario supera el tope de $" + tope.setScale(2, RoundingMode.HALF_UP),
                    salarioMensual, BigDecimal.ZERO);
        }
        if (anios < 1) {
            return new ResultadoQuincena25(false,
                    "Requiere al menos un anio con el mismo patrono",
                    salarioMensual, BigDecimal.ZERO);
        }

        BigDecimal monto = salarioMensual.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        return new ResultadoQuincena25(true,
                "Cumple los requisitos. No esta sujeta a ISSS, AFP ni renta.",
                salarioMensual, monto);
    }
}
