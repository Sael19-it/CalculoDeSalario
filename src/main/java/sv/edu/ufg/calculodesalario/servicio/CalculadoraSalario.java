package sv.edu.ufg.calculodesalario.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import sv.edu.ufg.calculodesalario.dominio.DescuentoDeLey;
import sv.edu.ufg.calculodesalario.dominio.Periodicidad;
import sv.edu.ufg.calculodesalario.dominio.RangoDeRenta;
import sv.edu.ufg.calculodesalario.dominio.ResultadoSalario;
import sv.edu.ufg.calculodesalario.repositorio.DescuentoDeLeyRepositorio;
import sv.edu.ufg.calculodesalario.repositorio.RangoDeRentaRepositorio;

/**
 * Calcula el salario liquido de un trabajador aplicando los descuentos de ley.
 *
 * Esta clase solo recibe numeros y devuelve numeros. No guarda nada en la base
 * de datos ni sabe nada de paginas web. Esa separacion es lo que permite
 * probarla de forma aislada con las pruebas automatizadas.
 */
@Service
public class CalculadoraSalario {

    private final RangoDeRentaRepositorio rangosDeRenta;
    private final DescuentoDeLeyRepositorio descuentosDeLey;

    /**
     * Spring entrega los repositorios ya construidos y conectados a la base de
     * datos. En ningun lugar del proyecto se escribe "new ...Repositorio()".
     */
    public CalculadoraSalario(RangoDeRentaRepositorio rangosDeRenta,
                              DescuentoDeLeyRepositorio descuentosDeLey) {
        this.rangosDeRenta = rangosDeRenta;
        this.descuentosDeLey = descuentosDeLey;
    }

    /** Version corta: calcula con la ley vigente el dia de hoy. */
    public ResultadoSalario calcular(BigDecimal salarioBruto, Periodicidad periodicidad) {
        return calcular(salarioBruto, periodicidad, LocalDate.now());
    }

    /**
     * Calcula el salario liquido con la ley vigente en una fecha determinada.
     *
     * Existe esta version con fecha explicita por dos razones: permite calcular
     * planillas de meses anteriores con la ley que regia entonces, y permite que
     * las pruebas automatizadas no dependan del dia en que se ejecuten.
     */
    public ResultadoSalario calcular(BigDecimal salarioBruto,
                                     Periodicidad periodicidad,
                                     LocalDate fecha) {

        // ---- Paso 1: lo que se le descuenta al trabajador por ISSS y AFP ----
        DescuentoDeLey afpDelTrabajador  = descuentosDeLey.buscarPorCodigo("AFP_TRABAJADOR", fecha);
        DescuentoDeLey isssDelTrabajador = descuentosDeLey.buscarPorCodigo("ISSS_TRABAJADOR", fecha);

        BigDecimal descuentoAfp  = afpDelTrabajador.calcularDescuento(salarioBruto, periodicidad);
        BigDecimal descuentoIsss = isssDelTrabajador.calcularDescuento(salarioBruto, periodicidad);

        // ---- Paso 2: la parte del salario sobre la que se cobra renta ----
        //
        // Este paso es el mas importante de toda la aplicacion y el que mas se
        // equivoca la gente. La renta NO se calcula sobre el salario bruto: las
        // cotizaciones al ISSS y a la AFP no son gravables, asi que primero se
        // restan. Con un salario de $1,000, calcularla sobre el bruto daria
        // $81.15 en lugar de los $60.45 correctos.
        BigDecimal salarioAfectoARenta = salarioBruto
                .subtract(descuentoAfp)
                .subtract(descuentoIsss);

        // ---- Paso 3: el descuento de renta segun el rango que corresponda ----
        RangoDeRenta rango = rangosDeRenta.buscarRangoQueAplica(
                periodicidad, salarioAfectoARenta, fecha);

        BigDecimal descuentoDeRenta = rango.calcularDescuentoDeRenta(salarioAfectoARenta);

        // ---- Paso 4: totales del trabajador ----
        BigDecimal totalDeDescuentos = descuentoAfp
                .add(descuentoIsss)
                .add(descuentoDeRenta);

        BigDecimal salarioLiquido = salarioBruto.subtract(totalDeDescuentos);

        // ---- Paso 5: lo que el patrono paga ademas del salario ----
        //
        // Estos aportes no salen del bolsillo del trabajador. Se suman al costo
        // que la empresa asume por tener a esa persona en planilla.
        DescuentoDeLey afpDelPatrono  = descuentosDeLey.buscarPorCodigo("AFP_PATRONO", fecha);
        DescuentoDeLey isssDelPatrono = descuentosDeLey.buscarPorCodigo("ISSS_PATRONO", fecha);

        BigDecimal aporteAfpDelPatrono  = afpDelPatrono.calcularDescuento(salarioBruto, periodicidad);
        BigDecimal aporteIsssDelPatrono = isssDelPatrono.calcularDescuento(salarioBruto, periodicidad);

        BigDecimal costoTotalParaElPatrono = salarioBruto
                .add(aporteAfpDelPatrono)
                .add(aporteIsssDelPatrono);

        return new ResultadoSalario(
                salarioBruto, periodicidad,
                descuentoAfp, descuentoIsss,
                salarioAfectoARenta, descuentoDeRenta,
                totalDeDescuentos, salarioLiquido,
                aporteAfpDelPatrono, aporteIsssDelPatrono, costoTotalParaElPatrono);
    }
}
