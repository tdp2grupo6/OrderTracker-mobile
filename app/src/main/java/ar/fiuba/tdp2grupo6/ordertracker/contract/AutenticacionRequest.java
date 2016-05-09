package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AutenticacionRequest {

    public String username;
    public String password;

    public AutenticacionRequest() {
        super();
    }

    public AutenticacionRequest(String username, String password) {
        super();

        this.username = username;
        this.password = password;
    }

    public String empaquetar() {
        //{"username": "vendedor", "password": "vendedor", "app": "Mobile"}

        String ret = "";
        try {
            JSONObject json = new JSONObject();
            json.put("username", this.username);
            json.put("password", this.password);
            json.put("app", "Mobile");

            ret = json.toString();
        } catch (Exception e) {
            ret = "";
        }

        return ret;
    }


}
