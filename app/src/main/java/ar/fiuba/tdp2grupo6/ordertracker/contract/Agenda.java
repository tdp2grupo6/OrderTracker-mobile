package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Agenda {

    //public long id;
    //public Date fechaRealizado;
    //public short estado;

    public Map<Integer, ArrayList<AgendaItem>> clienteByDay;


    public Agenda() {
        super();

        //this.id = 0;
        //this.fechaRealizado = new Date();
        //this.estado = 0;
        this.clienteByDay = new HashMap<Integer, ArrayList<AgendaItem>>();
    }

    public void addAgendaItem(AgendaItem agendaItem) {
        //organiza por dia
        ArrayList<AgendaItem> list = clienteByDay.get(agendaItem.diaId);
        if (list == null) {
            list = new ArrayList<AgendaItem>();
            clienteByDay.put(agendaItem.diaId, list);
        }
        list.add(agendaItem);
    }

}
