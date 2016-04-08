package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by dgacitua on 30-03-16.
 */
public class ProductoImagen {
    public JSONObject json;

    public long id;
    public long productoId;
    public String tipo;
    public String path;

    public ProductoImagen() {
        super();
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

}
