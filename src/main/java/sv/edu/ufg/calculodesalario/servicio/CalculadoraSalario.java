package sv.edu.ufg.calculodesalario.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import sv.edu.ufg.calculodesalario.dominio.ParametroCotizacion;
import sv.edu.ufg.calculodesalario.dominio.Periodicidad;
import sv.edu.ufg.calculodesalario.dominio.ResultadoSalario;
import sv.edu.ufg.calculodesalario.dominio.TramoIsr;
import sv.edu.ufg.calculodesalario.repositorio.CotizacionRepositorio;
import sv.edu.ufg.calculodesalario.repositorio.TramoIsrRepositorio;

@Service
public class CalculadoraSalario {

    private final TramoIsrRepositorio tramos;
    private final CotizacionRepositorio cotizaciones;

    public CalculadoraSalario(TramoIsrRepositorio tramos, CotizacionRepositorio cotizaciones) {
        this.tramos = tramos;
        this.cotizaciones = cotizaciones;
    }

    public ResultadoSalario calcular(BigDecimal salarioBruto, Periodicidad periodicidad) {
        return calcular(salarioBruto, periodicidad, LocalDate.now());
    }

    public ResultadoSalario calcular(BigDecimal salarioBruto, Periodicidad periodicidad, LocalDate fecha) {

        // 1. Cotizaciones del trabajador
        ParametroCotizacion afpLab = cotizaciones.buscarPorCodigo("AFP_LAB", fecha);
        ParametroCotizacion isssLab = cotizaciones.buscarPorCodigo("ISSS_LAB", fecha);

        BigDecimal montoAfp = afpLab.calcular(salarioBruto, periodicidad);
        BigDecimal montoIsss = isssLab.calcular(salarioBruto, periodicidad);

        // 2. Renta imponible = bruto menos cotizaciones
        BigDecimal rentaImponible = salarioBruto.subtract(montoAfp).subtract(montoIsss);

        // 3. ISR segun el tramo vigente
        TramoIsr tramo = tramos.buscarTramo(periodicidad, rentaImponible, fecha);
        BigDecimal montoIsr = tramo.calcularImpuesto(rentaImponible);

        // 4. Totales del trabajador
        BigDecimal totalDescuentos = montoAfp.add(montoIsss).add(montoIsr);
        BigDecimal salarioLiquido = salarioBruto.subtract(totalDescuentos);

        // 5. Costo patronal
        ParametroCotizacion afpPat = cotizaciones.buscarPorCodigo("AFP_PAT", fecha);
        ParametroCotizacion isssPat = cotizaciones.buscarPorCodigo("ISSS_PAT", fecha);

        BigDecimal montoAfpPat = afpPat.calcular(salarioBruto, periodicidad);
        BigDecimal montoIsssPat = isssPat.calcular(salarioBruto, periodicidad);
        BigDecimal costoTotal = salarioBruto.add(montoAfpPat).add(montoIsssPat);

        return new ResultadoSalario(
                salarioBruto, periodicidad,
                montoAfp, montoIsss, rentaImponible, montoIsr,
                totalDescuentos, salarioLiquido,
                montoAfpPat, montoIsssPat, costoTotal);
    }
}
