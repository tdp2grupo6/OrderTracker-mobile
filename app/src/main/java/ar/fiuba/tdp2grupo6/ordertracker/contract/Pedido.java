package ar.fiuba.tdp2grupo6.ordertracker.contract;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dgacitua on 30-03-16.
 */
public class Pedido {

    public static final int ESTADO_PENDIENTE = 0; //SIN GRABAR
    public static final int ESTADO_CONFIRMADO = 1; //GRABADO SIN SINCRONIZAR
    public static final int ESTADO_ACEPTADO = 2; //GRABADO, SINCRONIZADO Y ACEPTADO
    public static final int ESTADO_RECHAZADO = 3; //GRABADO, SINCRONIZADO Y RECHAZADO
    public static final int ESTADO_CORREGIDO = 4; //GRABADO, SINCRONIZADO Y CON DIFERENCIAS DE STOCK (HAY QUE RECHAZAR O CONFIRMAR)

    public long id;
    public long clienteId;
    //public ArrayList<Producto> catalogo = new ArrayList<Producto>();
    public Map<String, PedidoItem> items = new HashMap<String, PedidoItem>();
    public double importe;
    public short estado;

}
