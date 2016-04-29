package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

public class AgendaItem {

    public long id;
    public long clienteId;
    public int diaId;

    public Cliente cliente;

    public AgendaItem() {
        super();

        this.id = 0;
        this.clienteId = 0;
        this.diaId = -1;
    }

    public AgendaItem(int diaId, long clienteId) throws JSONException {
        super();

        this.id = 0;
        this.diaId = diaId;
        this.clienteId = clienteId;
    }
}
