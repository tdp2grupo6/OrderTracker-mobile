package ar.fiuba.tdp2grupo6.ordertracker.contract;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClienteTest {

    private static final long FAKE_CLIENTE_ID = 8;
    private static final String FAKE_CLIENTE_NOMBRE = "Nombre";
    private static final String FAKE_CLIENTE_DIRECCION = "Direccion 1234, CABA";

    private String clienteString;
    private JSONObject clienteJson;

    @Before
    public void setUp() throws JSONException{
        clienteJson =  new JSONObject();
        clienteJson.put("ID", FAKE_CLIENTE_ID);
        clienteJson.put("nombreCompleto", FAKE_CLIENTE_NOMBRE);
        clienteJson.put("direccion", FAKE_CLIENTE_DIRECCION);
        clienteJson.put("telefono", FAKE_CLIENTE_DIRECCION);
        clienteJson.put("email", FAKE_CLIENTE_DIRECCION);
        clienteJson.put("latitud", -34.5887297);
        clienteJson.put("longitud", -58.3966085);

        clienteString = clienteJson.toString();
    }

    @Test
    public void testClienteConstructorStringHappyCase() throws JSONException {
        Cliente cliente = new Cliente(clienteString);

        assertThat(cliente.id, is(FAKE_CLIENTE_ID));
        assertThat(cliente.nombreCompleto, is(FAKE_CLIENTE_NOMBRE));
        assertThat(cliente.direccion, is(FAKE_CLIENTE_DIRECCION));
        //assertThat(cliente.json, is(clienteJson));
        //assertThat(cliente.toString(), is(clienteString));
    }

    @Test
    public void testClienteConstructorJsonHappyCase() throws JSONException{
        Cliente cliente = new Cliente(clienteJson);

        assertThat(cliente.id, is(FAKE_CLIENTE_ID));
        assertThat(cliente.nombreCompleto, is(FAKE_CLIENTE_NOMBRE));
        assertThat(cliente.direccion, is(FAKE_CLIENTE_DIRECCION));
        //assertThat(cliente.toString(), is(clienteString));
    }
}