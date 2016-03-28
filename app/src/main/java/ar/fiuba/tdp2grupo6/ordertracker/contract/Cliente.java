package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

public class Cliente {

	public JSONObject json;

	public long id;
	public String nombre;
	public String direccion;
	public String email;
	public String telefono;
	public Double lat;
	public Double lng;


	public Cliente() {
		super();
	}

	public Cliente(String str) throws JSONException {
		super();

		this.json = new JSONObject(str);
		this.id = json.optLong("ID");
		this.nombre = json.getString("nombreCompleto");
		this.direccion = json.getString("direccion");
		this.telefono = json.getString("telefono");
		this.email = json.getString("email");
		this.lat = json.getDouble("latitud");
		this.lng = json.getDouble("longitud");
	}

	public Cliente(JSONObject json) throws JSONException {
		super();

		this.json = json;
		this.id = json.optLong("ID");
		this.nombre = json.getString("nombreCompleto");
		this.direccion = json.getString("direccion");
		this.telefono = json.getString("telefono");
		this.email = json.getString("email");
		this.lat = json.getDouble("latitud");
		this.lng = json.getDouble("longitud");
	}

	@Override
	public String toString() {
		return this.json.toString();
	}
}
