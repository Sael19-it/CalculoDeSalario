package sv.edu.ufg.calculodesalario;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import sv.edu.ufg.calculodesalario.dominio.ResultadoAguinaldo;
import sv.edu.ufg.calculodesalario.dominio.ResultadoQuincena25;
import sv.edu.ufg.calculodesalario.dominio.ResultadoVacaciones;
import sv.edu.ufg.calculodesalario.servicio.CalculadoraPrestaciones;

@SpringBootTest
class CalculadoraPrestacionesTest {

    private static final BigDecimal MIL = new BigDecimal("1000");

    @Autowired
    private CalculadoraPrestaciones calculadora;

    @Test
    @DisplayName("Aguinaldo: los tres rangos de antiguedad del Art. 198")
    void rangosAguinaldo() {
        assertThat(calculadora.calcularAguinaldo(MIL, 1, 0).diasCorrespondientes()).isEqualTo(15);
        assertThat(calculadora.calcularAguinaldo(MIL, 2, 0).diasCorrespondientes()).isEqualTo(15);
        assertThat(calculadora.calcularAguinaldo(MIL, 3, 0).diasCorrespondientes()).isEqualTo(19);
        assertThat(calculadora.calcularAguinaldo(MIL, 9, 0).diasCorrespondientes()).isEqualTo(19);
        assertThat(calculadora.calcularAguinaldo(MIL, 10, 0).diasCorrespondientes()).isEqualTo(21);
        assertThat(calculadora.calcularAguinaldo(MIL, 25, 0).diasCorrespondientes()).isEqualTo(21);
    }

    @Test
    @DisplayName("Aguinaldo: monto con cinco anios de servicio")
    void montoAguinaldo() {
        ResultadoAguinaldo r = calculadora.calcularAguinaldo(MIL, 5, 0);

        assertThat(r.salarioDiario()).isEqualByComparingTo("33.3333");
        assertThat(r.esProporcional()).isFalse();
        assertThat(r.monto()).isEqualByComparingTo("633.33");
    }

    @Test
    @DisplayName("Aguinaldo: con menos de un anio se paga proporcional")
    void aguinaldoProporcional() {
        ResultadoAguinaldo r = calculadora.calcularAguinaldo(MIL, 0, 180);

        assertThat(r.esProporcional()).isTrue();
        assertThat(r.monto()).isEqualByComparingTo("246.58");
    }

    @Test
    @DisplayName("Vacaciones: quince dias mas el recargo del treinta por ciento")
    void vacaciones() {
        ResultadoVacaciones r = calculadora.calcularVacaciones(MIL);

        assertThat(r.salarioBase()).isEqualByComparingTo("500.00");
        assertThat(r.recargo()).isEqualByComparingTo("150.00");
        assertThat(r.total()).isEqualByComparingTo("650.00");
    }

    @Test
    @DisplayName("Horas extra: la diurna se paga al doble")
    void horaExtraDiurna() {
        assertThat(calculadora.calcularHorasExtra(MIL, new BigDecimal("1"), "EXTRA_DIURNA")
                .factor()).isEqualByComparingTo("2.000");
    }

    @Test
    @DisplayName("Horas extra: la nocturna paga mas que la diurna")
    void horaExtraNocturna() {
        BigDecimal unaHora = new BigDecimal("1");
        BigDecimal diurna = calculadora.calcularHorasExtra(MIL, unaHora, "EXTRA_DIURNA").total();
        BigDecimal nocturna = calculadora.calcularHorasExtra(MIL, unaHora, "EXTRA_NOCTURNA").total();

        assertThat(nocturna).isGreaterThan(diurna);
    }

    @Test
    @DisplayName("Quincena 25: aplica al cumplir salario y antiguedad")
    void quincena25Aplica() {
        ResultadoQuincena25 r = calculadora.calcularQuincena25(MIL, 2);

        assertThat(r.aplica()).isTrue();
        assertThat(r.monto()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("Quincena 25: se rechaza por salario o por antiguedad")
    void quincena25NoAplica() {
        assertThat(calculadora.calcularQuincena25(new BigDecimal("2000"), 5).aplica()).isFalse();
        assertThat(calculadora.calcularQuincena25(MIL, 0).aplica()).isFalse();
    }
}
