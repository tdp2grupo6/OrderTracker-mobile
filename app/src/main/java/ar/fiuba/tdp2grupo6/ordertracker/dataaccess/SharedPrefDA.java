package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pablo on 07/05/16.
 */
public class SharedPrefDA {


    private static final String SP_ORDER_TRACKER = "SP_ORDER_TRACKER_AUTH";
    private static final String SP_KEY_TOKEN = "TOKEN";
    private static final String POST_METHOD = "POST";

    private static synchronized SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(SP_ORDER_TRACKER, Context.MODE_PRIVATE);
    }

    public static synchronized void deleteToken(Context context) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        //editor.remove(SP_KEY_TOKEN);
        editor.clear();
        editor.commit();
    }

    public static synchronized String getToken(Context context) {
        return getSharedPref(context).getString(SP_KEY_TOKEN, null);
    }

    public static synchronized void setToken(Context context,String token) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString(SP_KEY_TOKEN, token);
        editor.commit();
    }

}
