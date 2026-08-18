package sv.edu.ufg.calculodesalario.controlador;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sv.edu.ufg.calculodesalario.servicio.CalculadoraPrestaciones;

@Controller
public class PrestacionControlador {

    private final CalculadoraPrestaciones calculadora;

    public PrestacionControlador(CalculadoraPrestaciones calculadora) {
        this.calculadora = calculadora;
    }

    // ---------- Aguinaldo ----------
    @GetMapping("/aguinaldo")
    public String formAguinaldo() {
        return "aguinaldo";
    }

    @PostMapping("/aguinaldo")
    public String calcularAguinaldo(
            @RequestParam BigDecimal salario,
            @RequestParam int anios,
            @RequestParam(defaultValue = "0") int diasTrabajados,
            Model model) {
        model.addAttribute("salarioIngresado", salario);
        model.addAttribute("aniosIngresados", anios);
        model.addAttribute("diasIngresados", diasTrabajados);
        model.addAttribute("resultado",
                calculadora.calcularAguinaldo(salario, anios, diasTrabajados));
        return "aguinaldo";
    }

    // ---------- Vacaciones ----------
    @GetMapping("/vacaciones")
    public String formVacaciones() {
        return "vacaciones";
    }

    @PostMapping("/vacaciones")
    public String calcularVacaciones(@RequestParam BigDecimal salario, Model model) {
        model.addAttribute("salarioIngresado", salario);
        model.addAttribute("resultado", calculadora.calcularVacaciones(salario));
        return "vacaciones";
    }

    // ---------- Horas extra ----------
    @GetMapping("/horas-extra")
    public String formHorasExtra() {
        return "horas-extra";
    }

    @PostMapping("/horas-extra")
    public String calcularHorasExtra(
            @RequestParam BigDecimal salario,
            @RequestParam BigDecimal horas,
            @RequestParam String tipo,
            Model model) {
        model.addAttribute("salarioIngresado", salario);
        model.addAttribute("horasIngresadas", horas);
        model.addAttribute("tipoElegido", tipo);
        model.addAttribute("resultado",
                calculadora.calcularHorasExtra(salario, horas, tipo));
        return "horas-extra";
    }

    // ---------- Quincena 25 ----------
    @GetMapping("/quincena-25")
    public String formQuincena25() {
        return "quincena-25";
    }

    @PostMapping("/quincena-25")
    public String calcularQuincena25(
            @RequestParam BigDecimal salario,
            @RequestParam int anios,
            Model model) {
        model.addAttribute("salarioIngresado", salario);
        model.addAttribute("aniosIngresados", anios);
        model.addAttribute("resultado", calculadora.calcularQuincena25(salario, anios));
        return "quincena-25";
    }
}
