package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Visita {

    public long id;
    public long serverId;
    public long clienteId;
    public boolean tienePedido;
    public boolean tieneComentario;
    public Date fecha;
    public boolean enviado;

    public Cliente cliente;

    public Visita() {
        super();

        this.id = 0;
        this.serverId = 0;
        this.clienteId = 0;
        this.tieneComentario = false;
        this.tienePedido = false;
        this.fecha = new Date();
        this.enviado = false;
    }

    public Visita(long clienteId, Date fecha) throws JSONException {
        this();

        this.fecha = fecha;
        this.clienteId = clienteId;
    }

    public long procesarRespuesta(String response) {
        long serverId = 0;
        try {
            /*
            {
              "id": 3,
              "idVendedor": 2,
              "nombreVendedor": "OrderTracker, Vendedor",
              "idCliente": 9,
              "nombreCliente": "Nara, Wanda",
              "fechaProgramada": "2016-05-12T14:49:45-0300",
              "tipoVisita": "NO EXISTE","nombreVendedor": "OrderTracker, Vendedor",
              "idCliente": 9,
              "elementoVisita": "NO EXISTE"
            }
            */

            JSONObject obj = new JSONObject(response);

            serverId = obj.getLong("id");
        } catch (Exception e) {
            serverId = 0;
        }
        return serverId;
    }

    public String empaquetar() {
        String ret = "";
        try {
            /*
            {"cliente":{"id":9}, "fechaVisita": "2016-05-12T14:49:45-0300"}
            */

            JSONObject obj = new JSONObject();

            obj.put("cliente", new JSONObject().put("id", this.clienteId));
            obj.put("fechaVisita", Utils.date2string(this.fecha, false));

            ret = obj.toString();
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }
}
