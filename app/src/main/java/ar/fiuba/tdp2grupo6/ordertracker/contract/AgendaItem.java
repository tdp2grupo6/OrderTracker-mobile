package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

public class AgendaItem {

    public static final int DIA_DOMINGO = 0; //
    public static final int DIA_LUNES = 1; //
    public static final int DIA_MARTES = 2; //
    public static final int DIA_MIERCOLES = 3; //
    public static final int DIA_JUEVES = 4; //
    public static final int DIA_VIERNES = 5; //
    public static final int DIA_SABADO = 6; //

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
