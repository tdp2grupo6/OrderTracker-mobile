package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Cliente {

	public static final int ESTADO_NO_VISITADO = 1; //
	public static final int ESTADO_PENDIENTE = 2; //
	public static final int ESTADO_VISITADO = 3; //

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

	public String disponibilidad;
	public String validador;
	public JSONObject estadoServidor;
	public JSONArray agendaCliente;

	public int estado = 0;


	public Cliente() {
		super();
	}

	public Cliente(JSONObject json) throws JSONException {
		super();

		//{"agendaCliente":[3,4,6],"apellido":"Legrand","validador":"62e910ae-aedf-44e1-af40-19c8685a9458","direccion":"Defensa 1060","razonSocial":"Mirtha Legrand","telefono":"11 4512-5069","nombreCompleto":"Legrand, Mirtha","nombre":"Mirtha","id":11,"estado":{"nombre":"No visitado hoy","id":2,"tipo":"AMARILLO"},"email":"legrand@gmail.com","disponibilidad":"","latitud":-34.620147,"longitud":-58.371383}
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

		this.disponibilidad = json.getString("disponibilidad");
		this.validador = json.getString("validador");
		this.agendaCliente = json.getJSONArray("agendaCliente");
		this.estadoServidor = json.getJSONObject("estado");
		this.estado = ESTADO_NO_VISITADO;

	}

	public Cliente(String str) throws JSONException {
		this(new JSONObject(str));
	}


	@Override
	public String toString() {
		return this.json.toString();
	}

	public String mostrarEstado() {
		if (this.estado == ESTADO_VISITADO) {
			return "Cliente visitado";
		}
		else if (this.estado == ESTADO_PENDIENTE) {
			return "No visitado hoy";
		}
		else {
			return "Visita pendiente";
		}
	}
}
