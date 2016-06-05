package ar.fiuba.tdp2grupo6.ordertracker.contract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoItem {

    public long id;
    public int cantidad;
    public long productoId;

    public Producto producto;

    public int descuentoProcentajeAplicado() {
        int descuentoPorcentaje = 0;
        for (Descuento descuento: this.producto.getDescuentos()) {
            if (descuento.aplicaDescuentoCantidad(cantidad)) {
                descuentoPorcentaje = descuento.descuento;
                break;
            }
        }
        return descuentoPorcentaje;
    }

    public boolean tieneDescuento() {
        return this.producto.getDescuentos().size() > 0;
    }

}
