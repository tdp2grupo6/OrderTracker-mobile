package ar.fiuba.tdp2grupo6.ordertracker.contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pablo on 27/04/16.
 */
public class Utils {

    public static String date2string(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(date);
    }

    public static Date string2date(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean string2boolean(String str) {
        if (str!=null && str.toLowerCase().equals("true")) {
            return true;
        }
        else {
            return false;
        }
    }
}
