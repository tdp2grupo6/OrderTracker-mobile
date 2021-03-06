package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dgacitua on 23-04-16.
 */
public class Comentario {
	public JSONObject json;

	public long id;
	public long clienteId;
	public Date fechaComentario;
	public String razonComun;
	public String comentario;
	public boolean enviado;

	public long visitaId;
	public long visitaServerid;

	public Comentario() {
		super();
	}

	/*
	public Comentario(String str) throws JSONException {
		super();

		this.json = new JSONObject(str);
		this.id = json.optLong("id");
		this.clienteId = json.optLong("clienteId");
		this.fechaComentario = obtenerFecha(json, "fechaComentario");
		this.razonComun = json.getString("razonComun");
		this.comentario = json.getString("comentario");
		this.enviado = json.getBoolean("enviado");
	}

	public Comentario(JSONObject json) throws JSONException {
		super();

		this.json = json;
		this.id = json.optLong("id");
		this.clienteId = json.optLong("clienteId");
		this.fechaComentario = obtenerFecha(json, "fechaComentario");
		this.razonComun = json.getString("razonComun");
		this.enviado = json.getBoolean("enviado");
	}
	*/

	@Override
	public String toString() {
		return this.json.toString();
	}

	public Date obtenerFecha(JSONObject json, String data) {
		try {
			String dateStr = json.getString(data);
			return Utils.string2date(dateStr);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	// dgacitua: Se parsea manualmente el JSON para peticiones POST
	public String empaquetar() {
		String ret = "{'cliente':{'id':" + this.clienteId + "},"
				+ "'fechaComentario':'" + Utils.date2string(this.fechaComentario, false) + "',"
				+ "'razonComun':'" + this.razonComun + "',"
				+ "'comentario':'" + this.comentario + "'}";
		return ret;
	}
	*/

	public String empaquetar() {
		String ret = "";
		try {
            /*
            {'cliente':{'id':10}, 'razonComun': 'Otro', 'comentario': 'Sos capo, groso', 'visita':{'id':5}}
            */
			JSONObject obj = new JSONObject();
			obj.put("cliente", new JSONObject().put("id", this.clienteId));
			obj.put("fechaComentario", Utils.date2string(this.fechaComentario, false));
			obj.put("razonComun", this.razonComun);
			obj.put("comentario", this.comentario);
			if (visitaServerid > 0)
				obj.put("visita", new JSONObject().put("id", this.visitaServerid));

			ret = obj.toString();
		} catch (Exception e) {
			ret = "";
		}
		return ret;
	}


}
