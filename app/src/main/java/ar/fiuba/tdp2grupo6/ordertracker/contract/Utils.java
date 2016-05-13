package ar.fiuba.tdp2grupo6.ordertracker.contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by pablo on 27/04/16.
 */
public class Utils {

    public static String date2string(Date date, boolean noTimeData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sdf.setTimeZone(TimeZone.getDefault());
        String resp = sdf.format(date);
        if (noTimeData)
            resp = resp.substring(1,10);
        return resp;
    }

    public static Date string2date(String str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean string2boolean(String str) {
        return str != null && str.toLowerCase().equals("true");
    }
}
