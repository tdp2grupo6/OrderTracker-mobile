package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pablo on 23/04/16.
 */
public class Categoria {
    public long id = 0;
    public String nombre = "";
    public JSONObject json;

    public Categoria() {
        super();

        this.json = new JSONObject();

        try {
            this.json.put("codigo", this.id);
            this.json.put("nombre", this.nombre);
        } catch (Exception e) {}
    }

    public Categoria(String str) {
        super();

        try {
            this.json = new JSONObject(str);
            this.id = json.optLong("codigo");
            this.nombre = json.optString("nombre");
        } catch (Exception e) {}
    }

    public Categoria(JSONObject json) {
        super();

        this.json = json;
        this.id = json.optLong("codigo");
        this.nombre = json.optString("nombre");
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
