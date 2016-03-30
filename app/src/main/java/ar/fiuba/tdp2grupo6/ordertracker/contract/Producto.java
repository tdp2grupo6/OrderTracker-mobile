package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dgacitua on 30-03-16.
 */
public class Producto {
    public JSONObject json;

    public long id;
    public String nombre;
    public String marca;
    public String caracteristicas;
    public Double precio;
    public int stock;

    public Producto() {
        super();
    }

    public Producto(String str) throws JSONException {
        super();

        this.json = new JSONObject(str);
        this.id = json.optLong("id");
        this.nombre = json.getString("nombre");
        this.marca = json.getString("marca");
        this.caracteristicas = json.getString("caracteristicas");
        this.precio = json.getDouble("precio");
        this.stock = json.getInt("stock");
    }

    public Producto(JSONObject json) throws JSONException {
        super();

        this.json = json;
        this.id = json.optLong("id");
        this.nombre = json.getString("nombre");
        this.marca = json.getString("marca");
        this.caracteristicas = json.getString("caracteristicas");
        this.precio = json.getDouble("precio");
        this.stock = json.getInt("stock");
    }

    @Override
    public String toString() {
        return this.json.toString();
    }
}
