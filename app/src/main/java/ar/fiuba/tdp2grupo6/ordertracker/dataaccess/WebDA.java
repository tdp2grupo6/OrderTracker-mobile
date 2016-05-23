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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionRequest;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Push;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Visita;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
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

	private static final String STRING_RESPONSE_METHOD = "STRING";
	private static final String BITMAP_RESPONSE_METHOD = "BITMAP";

	private Context mContext;

	private String mUrlLocalEndpoint = "http://192.168.1.250:8080/OrderTracker/";	//"http://localhost:8080/OrderTracker/";
	private String mUrlRemoteEndpoint = "http://ordertracker-tdp2grupo6.rhcloud.com/";

	private String mUrlEndpoint = mUrlRemoteEndpoint;

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

	private String readResponseString(InputStream in) throws IOException{
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();

		if (in != null) {
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
		}

		return response.toString();
	}

	private Bitmap readResponseBitmap(InputStream in) throws IOException{
		Bitmap bitmap = null;

		if (in != null) {
			bitmap = BitmapFactory.decodeStream(in);
		}

		return bitmap;
	}


	private ResponseObject makeRequest(String targetURL, String requestType, String responseType, HashMap<String, String> headerMap, String entityString)
			throws ServiceException, AutorizationException {

		ResponseObject response = new ResponseObject();
		try {

			URL url = new URL(targetURL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {

				//connection.setRequestProperty("Authorization", "Bearer " + token);
				urlConnection.setRequestMethod(requestType);
				urlConnection.setConnectTimeout(ConnectionTimeout);
				urlConnection.setReadTimeout(SocketTimeout);
				urlConnection.setDoOutput(false);
				urlConnection = setHeaderData(urlConnection, headerMap);
				urlConnection = setEntity(urlConnection, entityString);
				urlConnection.connect();

				// get stream
				if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {        // || urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED || urlConnection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
					if (responseType == STRING_RESPONSE_METHOD) {
						String responseString = readResponseString(urlConnection.getInputStream());
						response.setData(responseString);
					} else if (responseType == BITMAP_RESPONSE_METHOD) {
						Bitmap responseBitmap = readResponseBitmap(urlConnection.getInputStream());
						response.setBitmap(responseBitmap);
					}
				} else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
					throw new AutorizationException(urlConnection.getResponseMessage());
					//throw new ServiceException(urlConnection.getResponseMessage(), response, ServiceExceptionType.AUTORIZATION);
				} else {
					String errorString = readResponseString(urlConnection.getErrorStream());
					response.setError(errorString);
					throw new ServiceException(errorString, response, ServiceExceptionType.INTERNAL);
				}
			} catch (AutorizationException ae) {
				throw ae;
			} catch (ProtocolException e) {
				throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.CONEXION);
			} catch (SocketTimeoutException e) {
				throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.TIMEOUT);
			} catch (IOException e) {
				throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.APPLICATION);
			} catch (IllegalStateException e) {
				throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.APPLICATION);
			} finally {
				urlConnection.disconnect();
			}
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	private void validateAutentication(AutenticacionResponse autenticacionResponse) throws ServiceException {
		if (!(autenticacionResponse != null && autenticacionResponse.accessToken != null))
			throw new ServiceException(mContext.getString(R.string.error_no_logued_in), null, ServiceExceptionType.AUTORIZATION);
	}

	// login a la aplicacion
	public ResponseObject sendAutenticar(AutenticacionRequest autenticacion)
			throws ServiceException, AutorizationException {

		ResponseObject response = null;
		try {
			String webMethod = "login";
			String targetURL = mUrlEndpoint + webMethod;

			String body = autenticacion.empaquetar();
			//Log.d("OT-LOG", "POSTeando Comentario " + autenticacion.username + ": " + body);

			// realiza la llamada al servicio
			response = makeRequest(targetURL, POST_METHOD, STRING_RESPONSE_METHOD, null, body);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}


	// Obtiene la agenda para el usuario
	public ResponseObject getAgenda(AutenticacionResponse autenticacionResponse)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = "agenda/semana";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, STRING_RESPONSE_METHOD, headers, null);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	// Obtiene los filtros disponibles para el usuario
	public ResponseObject getClientes(AutenticacionResponse autenticacionResponse)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {

			String webMethod = "cliente";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, STRING_RESPONSE_METHOD, headers, null);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	// Obtiene los filtros disponibles para el usuario
	public ResponseObject getProductos(AutenticacionResponse autenticacionResponse)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = "producto";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, STRING_RESPONSE_METHOD, headers, null);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	// Obtiene las imagenes para un producto
	public ResponseObject getProductosImagen(AutenticacionResponse autenticacionResponse, String rutaImagen)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = rutaImagen;
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, BITMAP_RESPONSE_METHOD, headers, null);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	// Obtiene las imagenes miniatura para un producto
	public ResponseObject getProductosImagenMiniatura(AutenticacionResponse autenticacionResponse, String rutaMiniatura)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = rutaMiniatura;
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			// realiza la llamada al servicio
			response = makeRequest(targetURL, GET_METHOD, BITMAP_RESPONSE_METHOD, headers, null);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	public ResponseObject sendVisita(AutenticacionResponse autenticacionResponse, Visita visita)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = "visita";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			String body = visita.empaquetar();

			// realiza la llamada al servicio
			response = makeRequest(targetURL, POST_METHOD, STRING_RESPONSE_METHOD, headers, body);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	public ResponseObject sendPushToken(AutenticacionResponse autenticacionResponse, Push push)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = "vendedor/push-token";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			String body = push.empaquetar();

			// realiza la llamada al servicio
			response = makeRequest(targetURL, POST_METHOD, STRING_RESPONSE_METHOD, headers, body);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	public ResponseObject sendComentario(AutenticacionResponse autenticacionResponse, Comentario comentario)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = "comentario";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			String body = comentario.empaquetar();
			//Log.d("OT-LOG", "POSTeando Comentario " + comentario.id + ": " + body);

			// realiza la llamada al servicio
			response = makeRequest(targetURL, POST_METHOD, STRING_RESPONSE_METHOD, headers, body);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}

	public ResponseObject sendPedido(AutenticacionResponse autenticacionResponse, Pedido pedido)
			throws ServiceException, AutorizationException {
		validateAutentication(autenticacionResponse);

		ResponseObject response = null;
		try {
			String webMethod = "pedido";
			String targetURL = mUrlEndpoint + webMethod;

			//Agrega el header de autenticacion
			HashMap<String, String> headers = new HashMap<>();
			headers.put(autenticacionResponse.getAutenticationHeaderKey(), autenticacionResponse.getAutenticationHeaderValue());

			String body = pedido.empaquetar();
			//Log.d("OT-LOG", "POSTeando Pedido " + pedido.id + ": " + body);

			// realiza la llamada al servicio
			response = makeRequest(targetURL, POST_METHOD, STRING_RESPONSE_METHOD, headers, body);
		} catch (AutorizationException ae) {
			throw ae;
		} catch (ServiceException se) {
			throw se;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), response, ServiceExceptionType.APPLICATION);
		}
		return response;
	}
}
