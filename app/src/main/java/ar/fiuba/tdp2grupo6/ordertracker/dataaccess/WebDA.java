package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException.ServiceExceptionType;


public class WebDA {

	//http://stackoverflow.com/questions/9767952/how-to-add-parameters-to-httpurlconnection-using-post

	private static final String TAG = "HttpUrlConnectionUtlity";

	private static final String GET_METHOD = "GET";


	private static final String POST_METHOD = "POST";
	private static final String PUT_METHOD = "PUT";
	private static final String HEAD_METHOD = "HEAD";
	private static final String DELETE_METHOD = "DELETE";
	private static final String TRACE_METHOD = "TRACE";
	private static final String OPTIONS_METHOD = "OPTIONS";

	private Context mContext;
	private String mUrlEndpoint = "http://ordertracker-tdp2grupo6.rhcloud.com/";
	private int ConnectionTimeout = 30000;
	private int SocketTimeout = 120000;

	public WebDA(Context mContext) {
		this.mContext = mContext;
	}

	public WebDA(Context mContext, int connectionTimeout, int socketTimeout) {
		this.mContext = mContext;
		ConnectionTimeout = connectionTimeout;
		SocketTimeout = socketTimeout;
	}

	public WebDA(Context mContext, String mUrlEndpoint, int connectionTimeout, int socketTimeout) {
		this.mContext = mContext;
		this.mUrlEndpoint = mUrlEndpoint;
		ConnectionTimeout = connectionTimeout;
		SocketTimeout = socketTimeout;
	}

	private HttpURLConnection setHeaderData(HttpURLConnection urlConnection, HashMap<String, String> headerMap) throws UnsupportedEncodingException {
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.setRequestProperty("Accept", "application/json");
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		} else {
			//Logger.w(TAG, "NO HEADER DATA TO APPEND ||NO HEADER DATA TO APPEND ||NO HEADER DATA TO APPEND");
		}
		return urlConnection;
	}

	private HttpURLConnection setEntity(HttpURLConnection urlConnection, String entityString) throws IOException {
		if (entityString != null) {
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			writer.write(entityString);
			writer.flush();
			writer.close();
			outputStream.close();
		} else {
			//Logger.w(TAG, "NO ENTITY DATA TO APPEND ||NO ENTITY DATA TO APPEND ||NO ENTITY DATA TO APPEND");
		}
		return urlConnection;
	}

	private String readResponseStream(InputStream in) throws IOException{
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();

		reader = new BufferedReader(new InputStreamReader(in));
		String line = "";
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}

		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return response.toString();
	}

	private ResponseObject makeRequest(String targetURL, String requestType, HashMap<String, String> headerMap, String entityString) throws ServiceException {

		ResponseObject response = new ResponseObject();
		try {

			URL url = new URL(targetURL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(requestType);
			urlConnection.setConnectTimeout(ConnectionTimeout);
			urlConnection.setReadTimeout(SocketTimeout);
			urlConnection.setDoOutput(false);
			urlConnection = setHeaderData(urlConnection, headerMap);
			urlConnection = setEntity(urlConnection, entityString);
			urlConnection.connect();

			// get stream
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String responseString = readResponseStream(urlConnection.getInputStream());
				response.setData(responseString);
			} else {
				String errorString = readResponseStream(urlConnection.getErrorStream());
				response.setError(errorString);
				throw new ServiceException(errorString, response, ServiceExceptionType.INTERNAL);
			}

			urlConnection.disconnect();

		} catch (ProtocolException e) {
			throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.CONEXION);
		} catch (SocketTimeoutException e) {
			throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.TIMEOUT);
		} catch (IOException e) {
			throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.APPLICATION);
		} catch (IllegalStateException e) {
			throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.APPLICATION);
		}

		return response;
	}

	// Obtiene los filtros disponibles para el usuario
	public ResponseObject getClientes() throws ServiceException {

		ResponseObject response = null;
		try {
			String webMethod = "cliente";
			String targetURL = mUrlEndpoint + webMethod;

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, null, null);

			return response; //validar_response(response);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
	}

	// Obtiene los filtros disponibles para el usuario
	public ResponseObject getProductos() throws ServiceException {

		ResponseObject response = null;
		try {
			String webMethod = "producto";
			String targetURL = mUrlEndpoint + webMethod;

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, null, null);

			return response; //validar_response(response);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
	}
}
