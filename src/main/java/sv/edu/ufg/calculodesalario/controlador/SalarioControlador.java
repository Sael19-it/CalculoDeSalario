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

/**
 * Atiende las paginas del calculo de salario y del historial.
 *
 * El controlador es la puerta de entrada: recibe lo que el usuario escribio en
 * el formulario, se lo pasa a la calculadora y entrega el resultado a la vista.
 * No hace cuentas por su parte.
 */
@Controller
public class SalarioControlador {

    private final CalculadoraSalario calculadora;
    private final HistorialRepositorio historial;

    public SalarioControlador(CalculadoraSalario calculadora,
                              HistorialRepositorio historial) {
        this.calculadora = calculadora;
        this.historial = historial;
    }

    /** Muestra el formulario vacio. */
    @GetMapping("/")
    public String mostrarFormulario(Model datosParaLaVista) {
        datosParaLaVista.addAttribute("periodicidades", Periodicidad.values());
        return "index";
    }

    /**
     * Recibe el formulario y devuelve la misma pagina con el resultado.
     *
     * Spring convierte solo el texto del formulario al tipo que pide cada
     * parametro: "1000" se vuelve BigDecimal y "MENSUAL" se vuelve el valor
     * del enum. Si llegara algo que no corresponde, Spring responde con un
     * error 400 sin que tengamos que validarlo a mano.
     */
    @PostMapping("/calcular")
    public String calcular(@RequestParam BigDecimal salario,
                           @RequestParam Periodicidad periodicidad,
                           Model datosParaLaVista) {

        ResultadoSalario resultado = calculadora.calcular(salario, periodicidad);

        // El guardado ocurre aqui y no dentro de la calculadora, para que la
        // calculadora siga siendo una funcion pura que solo hace cuentas.
        historial.guardar(resultado, 0);

        datosParaLaVista.addAttribute("periodicidades", Periodicidad.values());
        datosParaLaVista.addAttribute("salarioIngresado", salario);
        datosParaLaVista.addAttribute("periodicidadElegida", periodicidad);
        datosParaLaVista.addAttribute("resultado", resultado);

        return "index";   // el nombre del archivo en templates/, sin la extension
    }

    @GetMapping("/historial")
    public String verHistorial(Model datosParaLaVista) {
        datosParaLaVista.addAttribute("registros", historial.listarUltimos(20));
        return "historial";
    }

    @PostMapping("/historial/limpiar")
    public String limpiarHistorial() {
        historial.limpiar();
        return "redirect:/historial";
    }
}
