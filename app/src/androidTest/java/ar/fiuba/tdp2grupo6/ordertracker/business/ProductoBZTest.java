package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
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

/**
 * Created by dgacitua on 02-04-16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ProductoBZTest {
    private static String RESPONSE_CLIENTE = "[{\"id\": 1,\"nombre\": \"Mochila Deportiva Negra\",\"marca\": \"Adidas\",\"caracteristicas\": \"Mochila de alta capacidad y costura reforzada\",\"categoria\": [],\"rutaImagen\": \"\",\"stock\": 3,\"precio\": 849,\"estado\": \"No disponible\"},{\"id\": 2,\"nombre\": \"Bolso de la Seleccion\",\"marca\": \"Adidas\",\"caracteristicas\": \"Diseño y estilo de la Selección Argentina\",\"categoria\": [],\"rutaImagen\": \"\",\"stock\": 34,\"precio\": 1002.99,\"estado\": \"No disponible\"},{\"id\": 3,\"nombre\": \"Vestido Print\",\"marca\": \"A.Y. NOT DEAD\",\"caracteristicas\": \"Temporada otoño-invierno 2016\",\"categoria\": [],\"rutaImagen\": \"\",\"stock\": 12,\"precio\": 1433.99,\"estado\": \"No disponible\"}]";
    private DbHelper mDatabase;
    private MockWebServer mServer;
    private SqlDA mSqlDA;
    private WebDA mWebDA;
    private ProductoBZ mProductoBZ;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DbHelper.dbName);
        mDatabase = new DbHelper(getTargetContext(), null);
        this.mSqlDA = new SqlDA(getTargetContext(), mDatabase);

        mServer = new MockWebServer();
        this.mWebDA = new WebDA(getTargetContext(), mServer.url("/").toString(),30000, 120000);

        this.mProductoBZ = new ProductoBZ(getTargetContext(), this.mWebDA, this.mSqlDA);
    }

    @After
    public void tearDown() throws Exception {
        mDatabase.close();
        mServer.shutdown();
    }

    @Test
    public void testProductosSincronizar() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody(RESPONSE_CLIENTE));
        ArrayList<Producto> productosTest = this.mProductoBZ.sincronizar();

        //busca
        ArrayList<Producto> productosVal = mSqlDA.productoBuscar(0);

        assertThat(productosTest, is(not(nullValue())));
        assertThat(productosTest.size(), is(productosVal.size()));
    }

    @Test
    public void testProductosListar() throws Exception {
        //Prepara la data
        JSONArray data = new JSONArray(RESPONSE_CLIENTE);

        //busca, devuelve vacio
        ArrayList<Producto> productosTest = this.mProductoBZ.listar();

        assertThat(productosTest, is(not(nullValue())));
        assertThat(productosTest.size(), is(0));

        //ingresa el primero
        Producto producto = new Producto(data.getJSONObject(0));
        mSqlDA.productoGuardar(producto);

        //busca
        productosTest = this.mProductoBZ.listar();

        assertThat(productosTest, is(not(nullValue())));
        assertThat(productosTest.size(), is(1));

        //ingresa el segundo
        producto = new Producto(data.getJSONObject(1));
        mSqlDA.productoGuardar(producto);

        //busca
        productosTest = this.mProductoBZ.listar();

        assertThat(productosTest, is(not(nullValue())));
        assertThat(productosTest.size(), is(2));
    }
}
