package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dgacitua on 23-04-16.
 */
public class Comentario {
	public JSONObject json;

	public long id;
	public long idCliente;
	public Date fechaComentario;
	public String razonComun;
	public String comentario;

	public Comentario() {
		super();
	}

	public Comentario(String str) throws JSONException {
		super();

		this.json = new JSONObject(str);
		this.id = json.optLong("id");
		this.idCliente = json.optLong("idCliente");
		this.fechaComentario = obtenerFecha(json, "fechaComentario");
		this.razonComun = json.getString("razonComun");
		this.comentario = json.getString("comentario");
	}

	public Comentario(JSONObject json) throws JSONException {
		super();

		this.json = json;
		this.id = json.optLong("id");
		this.idCliente = json.optLong("idCliente");
		this.fechaComentario = obtenerFecha(json, "fechaComentario");
		this.razonComun = json.getString("razonComun");
		this.comentario = json.getString("comentario");
	}

	@Override
	public String toString() {
		return this.json.toString();
	}

	public Date obtenerFecha(JSONObject json, String data) {
		try {
			String dateStr = json.getString(data);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(dateStr);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
