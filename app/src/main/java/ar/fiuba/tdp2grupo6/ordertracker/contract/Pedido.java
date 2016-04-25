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
    //public Map<String, ArrayList<PedidoItem>> itemsByBrand = new HashMap<String, ArrayList<PedidoItem>>(); //por Marca
    public Map<String, HashMap<String, ArrayList<PedidoItem>>> itemsByCategoryBrand = new HashMap<String, HashMap<String, ArrayList<PedidoItem>>>(); //por Marca
    public ArrayList<Categoria> categorias = new ArrayList<Categoria>();
    public ArrayList<String> marcas = new ArrayList<String>();

    public void generateMaps() {
        itemsByCategory = new HashMap<String, ArrayList<PedidoItem>>(); //por CategoriaId
        itemsByCategoryBrand = new HashMap<String, HashMap<String, ArrayList<PedidoItem>>>(); //por CategoriaId

        itemsByCategory.put(String.valueOf(0), new ArrayList<PedidoItem>());
        for (PedidoItem pedidoItem: items.values()) {

            //Organiza por categoria
            addToCategoryList(0, pedidoItem);
            if (addToCategoryList(pedidoItem.producto.categoria.id, pedidoItem))
                categorias.add(pedidoItem.producto.categoria);

            //Organiza por categoria y marca
            if (addToCategoryBrandList(0, pedidoItem.producto.marca, pedidoItem))
                marcas.add(pedidoItem.producto.marca);
            addToCategoryBrandList(pedidoItem.producto.categoria.id, pedidoItem.producto.marca, pedidoItem);

        }
    }


    private boolean addToCategoryList(long categoriaId, PedidoItem pedidoItem) {
        boolean nueva = false;

        //organiza por categoria
        ArrayList<PedidoItem> list = itemsByCategory.get(String.valueOf(categoriaId));
        if (list == null) {
            nueva = true;

            list = new ArrayList<PedidoItem>();
            itemsByCategory.put(String.valueOf(categoriaId), list);
        }
        list.add(pedidoItem);

        return nueva;
    }

    private boolean addToCategoryBrandList(long categoriaId, String marca, PedidoItem pedidoItem) {
        boolean nueva = false;

        //organiza por categoria y marca
        HashMap<String, ArrayList<PedidoItem>> categoryBrandMap = itemsByCategoryBrand.get(String.valueOf(categoriaId));
        if (categoryBrandMap == null) {
            categoryBrandMap = new HashMap<String, ArrayList<PedidoItem>>();
            itemsByCategoryBrand.put(String.valueOf(categoriaId), categoryBrandMap);
        }

        ArrayList<PedidoItem> list = categoryBrandMap.get(marca);
        if (list == null) {
            nueva = true;
            list = new ArrayList<PedidoItem>();
            categoryBrandMap.put(marca, list);
        }
        list.add(pedidoItem);

        return nueva;
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

    public ArrayList<PedidoItem> getItems(long categoriaId, String marca) {
        ArrayList<PedidoItem> lista = new ArrayList<PedidoItem>();

        if (marca == null || marca.trim().length() == 0) {
            lista = this.itemsByCategory.get(String.valueOf(categoriaId));
        } else {
            HashMap<String, ArrayList<PedidoItem>> map = this.itemsByCategoryBrand.get(String.valueOf(categoriaId));
            if (map != null && map.containsKey(marca))
                lista = map.get(marca);
        }

        return lista;
    }

}
