package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Push {

    public String username;
    public String token;

    public Push() {
        super();

        this.username = "";
        this.token = "";
    }

    public Push(String username, String token) {
        this();

        this.username = username;
        this.token = token;
    }

    public String empaquetar() {
        String ret = "";
        try {
            /*
            {“username”: “<usernameVendedor>”, “token”:”<FirebasePushToken>”}
            */

            JSONObject obj = new JSONObject();

            obj.put("username", this.username);
            obj.put("token", this.token);

            ret = obj.toString();
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }
}
