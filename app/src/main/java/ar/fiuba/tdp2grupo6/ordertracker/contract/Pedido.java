package ar.fiuba.tdp2grupo6.ordertracker.contract;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dgacitua on 30-03-16.
 */
public class Pedido {

    public long id;
    public long clienteId;
    public Map<String, PedidoItem> productos = new HashMap<String, PedidoItem>();
    public double importe;
    public short estado;

}
