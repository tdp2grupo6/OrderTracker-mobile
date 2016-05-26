package ar.fiuba.tdp2grupo6.ordertracker.contract;

import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dgacitua on 30-03-16.
 */
public class Pedido {

    // dgacitua: Nuevos estados del pedido (validados con el cliente)
    public static final int ESTADO_NUEVO = 1; // Se crea un nuevo pedido en el app movil
    public static final int ESTADO_CONFIRMADO = 2; // Se confirma/acepta el pedido en el app movil, aún no se envía al servidor
    public static final int ESTADO_ENVIADO = 3; // El pedido fue enviado al servidor, queda pendiente la respuesta por parte del Administrador
    public static final int ESTADO_ACEPTADO = 4; // Se acepta el pedido por parte del Administrador y se asigna el stock solicitado
    public static final int ESTADO_DESPACHADO = 5; // Se despacha el pedido
    public static final int ESTADO_CANCELADO = 6; // El pedido no puede ser resuelto por el Administrador, por lo que se rechaza

    public long id;
    public long idServer;
    public long clienteId;
    public Date fechaRealizado;
    public Cliente cliente;
    public short estado;
    private double importe;
    private boolean dirtyImporte;

    public long visitaId;
    private long visitaServerId;

    public Map<String, PedidoItem> items;
    public Map<String, ArrayList<PedidoItem>> itemsByCategory; //por CategoriaId
    public Map<String, HashMap<String, ArrayList<PedidoItem>>> itemsByCategoryBrand; //por Marca
    public ArrayList<Categoria> categorias;
    public ArrayList<String> marcas;


    public Pedido() {
        super();

        this.id = 0;
        this.idServer = 0;
        this.clienteId = 0;
        this.fechaRealizado = new Date();
        this.cliente = null;
        this.estado = Pedido.ESTADO_NUEVO;
        this.importe = 0;
        this.dirtyImporte = false;

        this.items = new HashMap<String, PedidoItem>(); //por ID
        this.itemsByCategory = new HashMap<String, ArrayList<PedidoItem>>(); //por CategoriaId
        this.itemsByCategoryBrand = new HashMap<String, HashMap<String, ArrayList<PedidoItem>>>(); //por Marca
        this.categorias = new ArrayList<Categoria>();
        this.marcas = new ArrayList<String>();
    }

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

    public void deleteItem(long productoId) {
        PedidoItem pedidoItem = this.items.remove(String.valueOf(productoId));

        this.dirtyImporte = true;
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

    public String empaquetar() {
        String ret = "";
        try {
            /*
           {"cliente": {"id":9}, "fecha": "2016-05-12T14:49:45-0300",
           "elementos": [{"producto":{"id":4},"cantidad":6}, {"producto":{"id":2},"cantidad":2}],
           "visita": {"id":4}}
            */
            JSONObject obj = new JSONObject();
            obj.put("cliente", new JSONObject().put("id", this.clienteId));
            obj.put("fechaRealizado", Utils.date2string(this.fechaRealizado, false));

            JSONArray elementos = new JSONArray();
            for (PedidoItem pedidoItem: this.items.values()) {
                if (pedidoItem.cantidad > 0) {
                    JSONObject item = new JSONObject();
                    item.put("producto", new JSONObject().put("id", pedidoItem.productoId));
                    item.put("cantidad", pedidoItem.cantidad);
                    elementos.put(item);
                }
            }
            obj.put("elementos", elementos);

            if (visitaServerId > 0)
                obj.put("visita", new JSONObject().put("id", this.visitaServerId));

            ret = obj.toString();
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

}
