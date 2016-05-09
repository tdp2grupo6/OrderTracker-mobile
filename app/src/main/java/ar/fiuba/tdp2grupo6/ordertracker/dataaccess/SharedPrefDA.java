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

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public SharedPrefDA(Context context) {
        mContext = context;
        mSharedPreferences = this.mContext.getSharedPreferences(SP_ORDER_TRACKER, Context.MODE_PRIVATE);
    }

    public void deleteToken() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(SP_KEY_TOKEN);
        editor.commit();
    }

    public String getToken() {
        return mSharedPreferences.getString(SP_KEY_TOKEN, null);
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SP_KEY_TOKEN, token);
        editor.commit();
    }

}