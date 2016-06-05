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
public class Producto {
    public JSONObject json;

    public long id;
    public String nombre;
    public String marca;
    public String caracteristicas;
    public Categoria categoria;
    public Double precio;
    public int stock;
    public String estado;
    public String rutaImagen;
    public String rutaMiniatura;

    public Bitmap imagenMini;
    public Bitmap imagen;

    public JSONArray descuentosJson;
    private ArrayList<Descuento> descuentos;

    public Producto() {
        super();
    }

    public Producto(String str) throws JSONException {
        this(new JSONObject(str));
    }

    public Producto(JSONObject json) throws JSONException {
        super();

        this.json = json;
        this.id = json.optLong("id");
        this.nombre = json.getString("nombre");
        this.marca = json.getString("marca");
        this.caracteristicas = json.getString("caracteristicas");
        this.categoria = new Categoria(json.getJSONArray("categorias").getJSONObject(0));
        this.precio = json.getDouble("precio");
        this.stock = json.getInt("stock");
        this.estado = json.getJSONObject("estado").getString("tipo");
        this.rutaImagen = json.getString("rutaImagen");
        this.rutaMiniatura = json.getString("rutaMiniatura");

        //Me aseguro que nunca sea null el json
        try {
            this.descuentosJson = json.getJSONArray("descuentos");
        } catch (Exception e) {
            this.descuentosJson = new JSONArray();
        }
    }

    @Override
    public String toString() {
        return this.json.toString();
    }

    public String getNombreImagenMiniatura() {
        return "prod_img_mini_" + this.id + ".png";
    }

    public String getNombreImagen() {
        return "prod_img_" + this.id + ".png";
    }

    public ArrayList<Descuento> getDescuentos() {
        if (this.descuentos == null){
            this.descuentos = new ArrayList<Descuento>();
            for (int i = 0; i < descuentosJson.length(); i++) {
                try {
                    JSONObject descuentoJson = this.descuentosJson.getJSONObject(i);
                    Descuento descuento = new Descuento(descuentoJson);
                    this.descuentos.add(descuento);
                } catch (Exception e) {
                }
            }
        }
        return this.descuentos;
    }

    public boolean tieneDescuento() {
        return this.getDescuentos().size() > 0;
    }

    public String mostrarPrecio() {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "$" + formatter.format(precio);
    }

    public String mostrarStock() {
        return String.valueOf(stock);
    }

    public String mostrarCodigo() { return String.valueOf(id); }

    public String mostrarEstado() {
        if (this.estado.equals("SUSP")) {
            return "Suspendido";
        }
        else if (this.estado.equals("DISP")) {
            return "Disponible";
        }
        else {
            return "No disponible";
        }
    }
}
