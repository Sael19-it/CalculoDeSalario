package sv.edu.ufg.calculodesalario.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Un rango de la tabla de retencion de renta.
 *
 * La renta en El Salvador es un descuento por escalones: no se aplica un solo
 * porcentaje a todo el salario. Cada rango cobra su propio porcentaje, pero
 * unicamente sobre la parte del salario que sobrepasa el piso de ese rango.
 *
 * Ejemplo con la tabla mensual vigente. Un salario afecto a renta de $897.50
 * cae en el tercer rango, que va de $895.25 en adelante:
 *
 *     descuentoBase   = $60.00   (lo que ya se acumulo en los rangos previos)
 *     porcentaje      = 20%
 *     excedenteDesde  = $895.24
 *
 *     descuento = 60.00 + (897.50 - 895.24) x 20% = $60.45
 *
 * Los cinco valores provienen del Decreto Ejecutivo No. 10 del 30 de abril de
 * 2025 y se leen desde la base de datos, nunca estan escritos en el codigo.
 */
public record RangoDeRenta(
        BigDecimal salarioDesde,
        BigDecimal salarioHasta,
        BigDecimal descuentoBase,
        BigDecimal porcentaje,
        BigDecimal excedenteDesde) {

    /**
     * Calcula cuanto se le descuenta de renta a un salario que cae en este rango.
     *
     * Este metodo vive dentro del rango y no dentro del servicio porque el rango
     * ya tiene todos los datos necesarios para hacer la cuenta. Un objeto que
     * sabe operar con su propia informacion es mas facil de leer y de probar
     * que uno que solo guarda valores.
     */
    public BigDecimal calcularDescuentoDeRenta(BigDecimal salarioAfectoARenta) {

        BigDecimal parteQueSobrepasaElPiso = salarioAfectoARenta.subtract(excedenteDesde);

        // Proteccion ante un rango mal configurado en la base de datos.
        // Sin esta linea, un excedenteDesde mayor al salario produciria un
        // descuento negativo, es decir la aplicacion le "regalaria" dinero
        // al trabajador en lugar de descontarle.
        if (parteQueSobrepasaElPiso.compareTo(BigDecimal.ZERO) < 0) {
            parteQueSobrepasaElPiso = BigDecimal.ZERO;
        }

        return descuentoBase
                .add(parteQueSobrepasaElPiso.multiply(porcentaje))
                .setScale(2, RoundingMode.HALF_UP);   // a centavos exactos
    }
}
