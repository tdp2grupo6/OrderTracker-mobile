package ar.fiuba.tdp2grupo6.ordertracker.contract;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dgacitua on 30-03-16.
 */
public class Descuento {
    public JSONObject json;

    public long id;
    public String nombre;
    public long productoId;
    public String productoNombre;
    public int descuento;
    public int minimoProductos;
    public int maximoProductos;

    public Descuento() {
        super();
    }

    public Descuento(String str) throws JSONException {
        this(new JSONObject(str));
    }

    public Descuento(JSONObject json) throws JSONException {
        super();

        /*
        {
            "id": 1,
            "nombre": "",
            "idProducto": 1,
            "nombreProducto": "Mochila Deportiva Negra",
            "descuento": 5,
            "minimoProductos": 5,
            "maximoProductos": 9
	    }
        */

        this.json = json;
        this.id = json.optLong("id");
        this.nombre = json.getString("nombre");
        this.productoId = json.getLong("idProducto");
        this.productoNombre = json.getString("nombreProducto");
        this.descuento = json.getInt("descuento");
        this.minimoProductos = json.getInt("minimoProductos");
        this.maximoProductos = json.getInt("maximoProductos");
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

    public String getDescripcion() {
        return this.nombre + " - descuento " + this.descuento + "%: de " + this.minimoProductos + " a " + this.maximoProductos + " productos";
    }

    public boolean aplicaDescuentoCantidad(int cantidad) {
        return cantidad >= this.minimoProductos && cantidad <= this.maximoProductos;
    }
}
