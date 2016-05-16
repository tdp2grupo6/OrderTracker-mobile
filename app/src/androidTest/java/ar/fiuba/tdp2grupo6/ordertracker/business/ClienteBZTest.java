package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db.DbHelper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class ClienteBZTest {

    private static String RESPONSE_CLIENTE = "[{\"id\":1,\"nombreCompleto\":\"Luna, Silvina\",\"nombre\":\"Silvina\",\"apellido\":\"Luna\",\"razonSocial\":\"Silvina Luna\",\"direccion\":\"Las Heras 2850\",\"telefono\":\"\",\"email\":\"silvi@gmail.com\",\"latitud\":-34.5887297,\"longitud\":-58.3966085,\"estado\":{\"tipo\":\"ROJO\",\"nombre\":\"Visita pendiente\"}},{\"id\":2,\"nombreCompleto\":\"Rial, Jorge\",\"nombre\":\"Jorge\",\"apellido\":\"Rial\",\"razonSocial\":\"Jorge Rial\",\"direccion\":\"Monroe 1501\",\"telefono\":\"\",\"email\":\"jrial@hotmail.com\",\"latitud\":-34.552929,\"longitud\":-58.451036,\"estado\":{\"tipo\":\"ROJO\",\"nombre\":\"Visita pendiente\"}}]";
    private DbHelper mDatabase;
    private MockWebServer mServer;
    private SqlDA mSqlDA;
    private WebDA mWebDA;
    private ClienteBZ mClienteBZ;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DbHelper.dbName);
        mDatabase = new DbHelper(getTargetContext(), null);
        this.mSqlDA = new SqlDA(getTargetContext(), mDatabase);

        mServer = new MockWebServer();
        this.mWebDA = new WebDA(getTargetContext(), mServer.url("/").toString(),30000, 120000);

        this.mClienteBZ = new ClienteBZ(getTargetContext(), this.mWebDA, this.mSqlDA);
    }

    @After
    public void tearDown() throws Exception {
        mDatabase.close();
        mServer.shutdown();
    }

    @Test
    public void testClientesSincronizar() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(RESPONSE_CLIENTE));
        ArrayList<Cliente> clientesTest = this.mClienteBZ.sincronizar();

        //busca
        ArrayList<Cliente> clientesVal = mSqlDA.clienteBuscar(0, "", "");

        assertThat(clientesTest, is(not(nullValue())));
        assertThat(clientesTest.size(), is(clientesVal.size()));
    }

    @Test
    public void testClientesListar() throws Exception {
        //Prepara la data
        JSONArray data = new JSONArray(RESPONSE_CLIENTE);

        //busca, devuelve vacio
        ArrayList<Cliente> clientesTest = this.mClienteBZ.listar();

        assertThat(clientesTest, is(not(nullValue())));
        assertThat(clientesTest.size(), is(0));

        //ingresa el primero
        Cliente cliente = new Cliente(data.getJSONObject(0));
        mSqlDA.clienteGuardar(cliente);

        //busca
        clientesTest = this.mClienteBZ.listar();

        assertThat(clientesTest, is(not(nullValue())));
        assertThat(clientesTest.size(), is(1));

        //ingresa el segundo
        cliente = new Cliente(data.getJSONObject(1));
        mSqlDA.clienteGuardar(cliente);

        //busca
        clientesTest = this.mClienteBZ.listar();

        assertThat(clientesTest, is(not(nullValue())));
        assertThat(clientesTest.size(), is(2));
    }
}
