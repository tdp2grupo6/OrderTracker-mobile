package ar.fiuba.tdp2grupo6.ordertracker.contract;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
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
    public Double precio;
    public int stock;
    public String estado;
    public String rutaImagen;
    public String rutaMiniatura;

    public Bitmap imagenMini;
    public Bitmap imagen;

    public Producto() {
        super();
    }

    public Producto(String str) throws JSONException {
        super();

        // dgacitua: JSON de ejemplo (actualizado 08/04/2016)
        //{"id":1,"nombre":"Mochila Deportiva Negra","marca":"Adidas","caracteristicas":"Mochila de alta capacidad y costura reforzada","categorias":[{"codigo":1,"nombre":"Ropa Deportiva"}],"rutaImagen":"imagen/ver/2","rutaMiniatura":"imagen/miniatura/2","stock":3,"precio":849.0,"estado":{"tipo":"NODISP","nombre":"No disponible"}}

        this.json = new JSONObject(str);
        this.id = json.optLong("id");
        this.nombre = json.getString("nombre");
        this.marca = json.getString("marca");
        this.caracteristicas = json.getString("caracteristicas");
        this.precio = json.getDouble("precio");
        this.stock = json.getInt("stock");
        this.estado = json.getJSONObject("estado").getString("tipo");
        this.rutaImagen = json.getString("rutaImagen");
        this.rutaMiniatura = json.getString("rutaMiniatura");
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
        this.estado = json.getJSONObject("estado").getString("tipo");
        this.rutaImagen = json.getString("rutaImagen");
        this.rutaMiniatura = json.getString("rutaMiniatura");
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

    public String mostrarPrecio() {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return "$" + formatter.format(precio);
    }

    public String mostrarStock() {
        return String.valueOf(stock);
    }

    public String mostrarCodigo() { return String.valueOf(id); }        // TODO definir sistema codigo de productos

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
