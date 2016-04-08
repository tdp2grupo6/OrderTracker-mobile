package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

public class Cliente {

	public JSONObject json;

	public long id;
	public String nombreCompleto;
	public String nombre;
	public String apellido;
	public String razonSocial;
	public String direccion;
	public String email;
	public String telefono;
	public Double lat;
	public Double lng;
	public String estado;


	public Cliente() {
		super();
	}

	public Cliente(String str) throws JSONException {
		super();

		// dgacitua: JSON de ejemplo (actualizado 08/04/2016)
		// {"id":1,"nombreCompleto":"Luna, Silvina","nombre":"Silvina","apellido":"Luna","razonSocial":"Silvina Luna","direccion":"Las Heras 2850","telefono":"","email":"silvi@gmail.com","latitud":-34.5887297,"longitud":-58.3966085,"estado":{"tipo":"ROJO","nombre":"Visita pendiente"}}

		this.json = new JSONObject(str);
		this.id = json.optLong("id");
		this.nombreCompleto = json.getString("nombreCompleto");
		this.nombre = json.getString("nombre");
		this.apellido = json.getString("apellido");
		this.razonSocial = json.getString("razonSocial");
		this.direccion = json.getString("direccion");
		this.telefono = json.getString("telefono");
		this.email = json.getString("email");
		this.lat = json.getDouble("latitud");
		this.lng = json.getDouble("longitud");
		this.estado = json.getJSONObject("estado").getString("tipo");
	}

	public Cliente(JSONObject json) throws JSONException {
		super();

		this.json = json;
		this.id = json.optLong("id");
		this.nombreCompleto = json.getString("nombreCompleto");
		this.nombre = json.getString("nombre");
		this.apellido = json.getString("apellido");
		this.razonSocial = json.getString("razonSocial");
		this.direccion = json.getString("direccion");
		this.telefono = json.getString("telefono");
		this.email = json.getString("email");
		this.lat = json.getDouble("latitud");
		this.lng = json.getDouble("longitud");
		this.estado = json.getJSONObject("estado").getString("tipo");
	}

	@Override
	public String toString() {
		return this.json.toString();
	}

	public String mostrarEstado() {
		if (this.estado.equals("VERDE")) {
			return "Cliente visitado";
		}
		else if (this.estado.equals("AMARILLO")) {
			return "No visitado hoy";
		}
		else {
			return "Visita pendiente";
		}
	}
}
