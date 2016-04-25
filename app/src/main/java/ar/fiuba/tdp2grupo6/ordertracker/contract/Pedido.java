package ar.fiuba.tdp2grupo6.ordertracker.contract;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dgacitua on 30-03-16.
 */
public class Pedido {
    // dgacitua: Nuevos estados del pedido (validados con el cliente)
    public static final int ESTADO_NUEVO = 0; // Se crea un nuevo pedido en el app movil
    public static final int ESTADO_CONFIRMADO = 1; // Se confirma/acepta el pedido en el app movil, aún no se envía al servidor
    public static final int ESTADO_ENVIADO = 2; // El pedido fue enviado al servidor, queda pendiente la respuesta por parte del Administrador
    public static final int ESTADO_ACEPTADO = 3; // Se acepta el pedido por parte del Administrador y se asigna el stock solicitado
    public static final int ESTADO_DESPACHADO = 4; // Se despacha el pedido
    public static final int ESTADO_CANCELADO = 5; // El pedido no puede ser resuelto por el Administrador, por lo que se rechaza

    // Estados antiguos (DEPRECADOS)
    /*
    public static final int ESTADO_PENDIENTE = 0; //SIN GRABAR
    public static final int ESTADO_CONFIRMADO = 1; //GRABADO SIN SINCRONIZAR
    public static final int ESTADO_ACEPTADO = 2; //GRABADO, SINCRONIZADO Y ACEPTADO
    public static final int ESTADO_RECHAZADO = 3; //GRABADO, SINCRONIZADO Y RECHAZADO
    public static final int ESTADO_CORREGIDO = 4; //GRABADO, SINCRONIZADO Y CON DIFERENCIAS DE STOCK (HAY QUE RECHAZAR O CONFIRMAR)
    */

    public long id;
    public long clienteId;
    public Cliente cliente;
    public short estado;
    private double importe;
    private boolean dirtyImporte = false;

    public Map<String, PedidoItem> items = new HashMap<String, PedidoItem>(); //por ID
    public Map<String, ArrayList<PedidoItem>> itemsByCategory = new HashMap<String, ArrayList<PedidoItem>>(); //por CategoriaId
    public Map<String, ArrayList<PedidoItem>> itemsByBrand = new HashMap<String, ArrayList<PedidoItem>>(); //por Marca
    public ArrayList<Categoria> categorias = new ArrayList<Categoria>();
    public ArrayList<String> marcas = new ArrayList<String>();

    public void generateMaps() {
        itemsByCategory = new HashMap<String, ArrayList<PedidoItem>>(); //por CategoriaId
        itemsByBrand = new HashMap<String, ArrayList<PedidoItem>>(); //por Marca

        for (PedidoItem pedidoItem: items.values()) {

            //organiza por catedoria
            ArrayList<PedidoItem> categoryList = itemsByCategory.get(String.valueOf(pedidoItem.producto.categoria.id));
            if (categoryList == null) {
                categoryList = new ArrayList<PedidoItem>();
                itemsByCategory.put(String.valueOf(pedidoItem.producto.categoria.id), categoryList);
                categorias.add(pedidoItem.producto.categoria);
            }
            categoryList.add(pedidoItem);

            //organiza por marca
            ArrayList<PedidoItem> brandList = itemsByBrand.get(String.valueOf(pedidoItem.producto.marca));
            if (brandList == null) {
                brandList = new ArrayList<PedidoItem>();
                itemsByBrand.put(String.valueOf(pedidoItem.producto.marca), brandList);
                marcas.add(pedidoItem.producto.marca);
            }
            brandList.add(pedidoItem);
        }
    }

    public void updateItem(long productoId, int cantidad) {
        PedidoItem pedidoItem = this.items.get(String.valueOf(productoId));
        pedidoItem.cantidad = cantidad;

        this.dirtyImporte = true;
    }

    public double getImporte(boolean actualizar) {
        if (dirtyImporte || actualizar) {
            double nuevoImporte = 0;
            for (PedidoItem pedidoItem : items.values()) {
                nuevoImporte += pedidoItem.cantidad * pedidoItem.producto.precio;
            }
            this.importe = nuevoImporte;
            this.dirtyImporte = false;
        }
        return this.importe;
    }

}
