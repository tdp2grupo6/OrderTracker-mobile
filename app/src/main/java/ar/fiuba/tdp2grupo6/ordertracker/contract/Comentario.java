package ar.fiuba.tdp2grupo6.ordertracker.contract;

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

	public Comentario() {
		super();
	}

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

	@Override
	public String toString() {
		return this.json.toString();
	}

	public Date obtenerFecha(JSONObject json, String data) {
		try {
			String dateStr = json.getString(data);
			return Comentario.string2date(dateStr);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// dgacitua: Se parsea manualmente el JSON para peticiones POST
	public String empaquetar() {
		String ret = "{'cliente':{'id':" + this.clienteId + "},"
				+ "'fechaComentario':'" + Comentario.date2string(this.fechaComentario) + "',"
				+ "'razonComun':'" + this.razonComun + "',"
				+ "'comentario':'" + this.comentario + "'}";
		return ret;
	}

	// dgacitua: Métodos para parsear la fecha, compatibles con el Backend en Grails
	static public String date2string(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return sdf.format(date);
	}

	static public Date string2date(String str) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	static public boolean string2boolean(String str) {
		if (str!=null && str.toLowerCase().equals("true")) {
			return true;
		}
		else {
			return false;
		}
	}
}
