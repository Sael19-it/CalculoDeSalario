package sv.edu.ufg.calculodesalario.controlador;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sv.edu.ufg.calculodesalario.dominio.Periodicidad;
import sv.edu.ufg.calculodesalario.dominio.ResultadoSalario;
import sv.edu.ufg.calculodesalario.repositorio.HistorialRepositorio;
import sv.edu.ufg.calculodesalario.servicio.CalculadoraSalario;

@Controller
public class SalarioControlador {

    private final CalculadoraSalario calculadora;
    private final HistorialRepositorio historial;

    public SalarioControlador(CalculadoraSalario calculadora, HistorialRepositorio historial) {
        this.calculadora = calculadora;
        this.historial = historial;
    }

    @GetMapping("/")
    public String mostrarFormulario(Model model) {
        model.addAttribute("periodicidades", Periodicidad.values());
        return "index";
    }

    @PostMapping("/calcular")
    public String calcular(
            @RequestParam BigDecimal salario,
            @RequestParam Periodicidad periodicidad,
            Model model) {

        ResultadoSalario resultado = calculadora.calcular(salario, periodicidad);
        historial.guardar(resultado, 0);

        model.addAttribute("periodicidades", Periodicidad.values());
        model.addAttribute("salarioIngresado", salario);
        model.addAttribute("periodicidadElegida", periodicidad);
        model.addAttribute("resultado", resultado);

        return "index";
    }

    @GetMapping("/historial")
    public String verHistorial(Model model) {
        model.addAttribute("registros", historial.listarUltimos(20));
        return "historial";
    }

    @PostMapping("/historial/limpiar")
    public String limpiarHistorial() {
        historial.limpiar();
        return "redirect:/historial";
    }
}
