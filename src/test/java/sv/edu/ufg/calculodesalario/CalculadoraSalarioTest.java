package sv.edu.ufg.calculodesalario;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import sv.edu.ufg.calculodesalario.dominio.Periodicidad;
import sv.edu.ufg.calculodesalario.dominio.ResultadoSalario;
import sv.edu.ufg.calculodesalario.servicio.CalculadoraSalario;

@SpringBootTest
class CalculadoraSalarioTest {

    @Autowired
    private CalculadoraSalario calculadora;

    private ResultadoSalario mensual(String salario) {
        return calculadora.calcular(new BigDecimal(salario), Periodicidad.MENSUAL);
    }

    @Test
    @DisplayName("Salario bajo el minimo imponible no paga renta")
    void salarioSinRenta() {
        ResultadoSalario r = mensual("400");

        assertThat(r.descuentoAfp()).isEqualByComparingTo("29.00");
        assertThat(r.descuentoIsss()).isEqualByComparingTo("12.00");
        assertThat(r.salarioAfectoARenta()).isEqualByComparingTo("359.00");
        assertThat(r.descuentoDeRenta()).isEqualByComparingTo("0.00");
        assertThat(r.salarioLiquido()).isEqualByComparingTo("359.00");
    }

    @Test
    @DisplayName("Renta imponible justo bajo el tramo II sigue exenta")
    void salarioLimiteExento() {
        ResultadoSalario r = mensual("600");

        assertThat(r.salarioAfectoARenta()).isEqualByComparingTo("538.50");
        assertThat(r.descuentoDeRenta()).isEqualByComparingTo("0.00");
        assertThat(r.salarioLiquido()).isEqualByComparingTo("538.50");
    }

    @Test
    @DisplayName("Salario de mil dolares cae en el tramo III")
    void salarioTramoTres() {
        ResultadoSalario r = mensual("1000");

        assertThat(r.descuentoAfp()).isEqualByComparingTo("72.50");
        assertThat(r.descuentoIsss()).isEqualByComparingTo("30.00");
        assertThat(r.salarioAfectoARenta()).isEqualByComparingTo("897.50");
        assertThat(r.descuentoDeRenta()).isEqualByComparingTo("60.45");
        assertThat(r.salarioLiquido()).isEqualByComparingTo("837.05");
    }

    @Test
    @DisplayName("Salario alto cae en el tramo IV y el ISSS toca su techo")
    void salarioTramoCuatro() {
        ResultadoSalario r = mensual("2500");

        assertThat(r.descuentoAfp()).isEqualByComparingTo("181.25");
        assertThat(r.descuentoIsss()).isEqualByComparingTo("30.00");
        assertThat(r.descuentoDeRenta()).isEqualByComparingTo("363.77");
        assertThat(r.salarioLiquido()).isEqualByComparingTo("1924.98");
    }

    @Test
    @DisplayName("El ISSS nunca supera treinta dolares mensuales")
    void topeIsssMensual() {
        assertThat(mensual("1000").descuentoIsss()).isEqualByComparingTo("30.00");
        assertThat(mensual("5000").descuentoIsss()).isEqualByComparingTo("30.00");
        assertThat(mensual("99999").descuentoIsss()).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("La AFP no tiene tope maximo de cotizacion")
    void afpSinTope() {
        assertThat(mensual("5000").descuentoAfp()).isEqualByComparingTo("362.50");
        assertThat(mensual("10000").descuentoAfp()).isEqualByComparingTo("725.00");
    }

    @Test
    @DisplayName("En quincena el techo del ISSS se parte a la mitad")
    void topeIsssQuincenal() {
        ResultadoSalario r = calculadora.calcular(
                new BigDecimal("500"), Periodicidad.QUINCENAL);

        assertThat(r.descuentoIsss()).isEqualByComparingTo("15.00");
        assertThat(r.descuentoAfp()).isEqualByComparingTo("36.25");
        assertThat(r.salarioAfectoARenta()).isEqualByComparingTo("448.75");
    }

    @Test
    @DisplayName("El costo patronal suma AFP e ISSS del patrono")
    void costoPatronal() {
        ResultadoSalario r = mensual("1000");

        assertThat(r.aporteAfpDelPatrono()).isEqualByComparingTo("87.50");
        assertThat(r.aporteIsssDelPatrono()).isEqualByComparingTo("75.00");
        assertThat(r.costoTotalParaElPatrono()).isEqualByComparingTo("1162.50");
    }

    @Test
    @DisplayName("El ISR se calcula sobre la renta imponible, no sobre el bruto")
    void isrNoSeCalculaSobreBruto() {
        ResultadoSalario r = mensual("1000");

        // Si se calculara mal, sobre el bruto de 1000, daria 81.15
        assertThat(r.descuentoDeRenta()).isEqualByComparingTo("60.45");
        assertThat(r.descuentoDeRenta()).isNotEqualByComparingTo("81.15");
    }
}
