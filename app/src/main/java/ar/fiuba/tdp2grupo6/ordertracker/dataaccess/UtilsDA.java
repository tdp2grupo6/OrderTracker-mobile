package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import org.apache.http.cookie.Cookie;
//import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class UtilsDA {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static String ListToString(ArrayList<String> list, String separador) {
		String value = "";
		if (list != null && list.size() > 0) {
			value = TextUtils.join(separador, list);
		}

		return value;
	}

	public static ArrayList<String> StringToList(String value, String separador) {
		ArrayList<String> list = new ArrayList<String>();
		if (value != null && value.trim().length() > 0) {
			list = new ArrayList<String>(Arrays.asList(value.split(separador)));
		}

		return list;
	}

	public static int BooleanToInt(boolean value) {
		return (value ? 1 : 0);
	}

	public static boolean IntToBoolean(int value) {
		return (value == 0 ? false : true);
	}

	public static long DateTimeToUnixTime(Date value) {
		long unixdate = 0;
		if (value != null)
			unixdate = value.getTime() / 1000;
		return unixdate;
	}

	public static long DateToLong(Date date) {
		long longdate = 0;
		if (date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			longdate = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
		}
		return longdate;
	}

	public static Date LongToDate(long value) {
		Date date = null;
		if (value != 0) {
			Calendar c = Calendar.getInstance();
			int year = Integer.parseInt(String.valueOf(value).substring(0, 4));
			int month = Integer.parseInt(String.valueOf(value).substring(4, 6));
			int day = Integer.parseInt(String.valueOf(value).substring(6, 8));

			c.set(year, month, day, 0, 0, 0);
			date = c.getTime();
		}
		return date;
	}

	public static Date UnixTimeToDateTime(long value) {
		return new Date(value * 1000);
	}

	public static long DateToUnixStartTime(Calendar calendar) {
		if (calendar == null)
			calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0); // set hours to zero
		calendar.set(Calendar.MINUTE, 0); // set minutes to zero
		calendar.set(Calendar.SECOND, 0); // s
		return calendar.getTimeInMillis() / 1000;
	}

	public static long DateToUnixEndTime(Calendar calendar) {
		if (calendar == null)
			calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTimeInMillis() / 1000;
	}

	public static String convertStreamToString(InputStream is) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	/*
	public static String getCookieValue(DefaultHttpClient httpClient, String name) {
		String result = null;
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					result = cookie.getValue();
					break;
				}

			}
		}
		return (result);
	}
	*/

	/*
	 * public static String AddWhereCondition(String whereString, String
	 * newCondition, String conector) { if (whereString == null |
	 * whereString.length() == 0) whereString = newCondition; else whereString
	 * += " " + conector + " " + newCondition + "=?";
	 * 
	 * return whereString; }
	 */

	public static String AddWhereCondition(String where, String newCondition, String conector) {
		if (where == null || where.length() == 0)
			where = " WHERE " + newCondition;
		else
			where += " " + conector + " " + newCondition;

		return where;
	}

	public static String AddCondition(String where, String newCondition, String conector) {
		if (where == null || where.length() == 0)
			where = newCondition;
		else
			where += " " + conector + " " + newCondition;

		return where;
	}

	public static String ListOfPropertys(List<Object> lista, String property, String separator) {

		String response = "";

		try {
			for (Object myObject : lista) {
				Field propertyInfo = myObject.getClass().getField(property);
				separator = propertyInfo.getGenericType().toString() + separator;
			}
			response = response.substring(0, response.length() - 1);
		} catch (NoSuchFieldException e) {
			response = "";
		}

		return response;
	}

	public static String getStringFromCharset(String charsetName, String encodedString) {
		String normal = "";
		try {
			Charset charset = Charset.forName(charsetName);
			CharsetDecoder charsetDecoder = charset.newDecoder();
			byte[] array = encodedString.getBytes(charset.displayName());
			ByteBuffer byteBuffer = ByteBuffer.wrap(array);
			CharBuffer charBuffer = charsetDecoder.decode(byteBuffer);
			normal = charBuffer.toString();
		} catch (Exception e) {
			if (e != null)
				e.printStackTrace();
		}
		return normal;
	}

}