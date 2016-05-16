package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db.DbHelper;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SqlDATest {

    private DbHelper database;
    private SqlDA mDataBase;

    @Before
    public void setUp() throws Exception {
        getTargetContext().deleteDatabase(DbHelper.dbName);
        database = new DbHelper(getTargetContext(), null);

        this.mDataBase = new SqlDA(getTargetContext(), database);
    }

    @After
    public void tearDown() throws Exception {
        database.close();
    }

    @Test
    public void testClientesGuardar() throws Exception {
        //SqlDA mDataBase = new SqlDA(mMockContext);

        //graba
        Cliente cliente =  new Cliente();
        cliente.id = 1;
        cliente.nombreCompleto = "algo";
        cliente.direccion = "algomas";
        mDataBase.clienteGuardar(cliente);

        //busca
        ArrayList<Cliente> clientes = mDataBase.clienteBuscar(cliente.id, "", "");

        assertThat(clientes , is(not(nullValue())));
        assertThat(clientes.size(), is(1));
        assertThat(clientes.get(0).id, is(cliente.id));
        assertThat(clientes.get(0).nombreCompleto, is(cliente.nombreCompleto));
        assertThat(clientes.get(0).direccion, is(cliente.direccion));
    }

    @Test
    public void testClientesActualizar() throws Exception {
        //SqlDA mDataBase = new SqlDA(mMockContext);

        //graba
        Cliente cliente =  new Cliente();
        cliente.id = 1;
        cliente.nombreCompleto = "algo";
        cliente.direccion = "algomas";
        mDataBase.clienteGuardar(cliente);

        //actualiza
        cliente.nombreCompleto = "algo1";
        cliente.direccion = "algomas1";
        long cant = mDataBase.clienteActualizar(cliente);

        //busca
        ArrayList<Cliente> clientes = mDataBase.clienteBuscar(cliente.id, "", "");

        assertThat(cant, is((long)1));
        assertThat(clientes, is(not(nullValue())));
        assertThat(clientes.size(), is(1));
        assertThat(clientes.get(0).id, is(cliente.id));
        assertThat(clientes.get(0).nombreCompleto, is(cliente.nombreCompleto));
        assertThat(clientes.get(0).direccion, is(cliente.direccion));
    }

    @Test
    public void testClientesVaciar() throws Exception {
        //SqlDA mDataBase = new SqlDA(mMockContext);

        Cliente cliente =  new Cliente();
        cliente.id = 1;
        cliente.nombreCompleto = "algo";
        cliente.direccion = "algomas";

        mDataBase.clienteGuardar(cliente);
        mDataBase.clienteVaciar();
        ArrayList<Cliente> clientes = mDataBase.clienteBuscar(cliente.id, "", "");


        assertThat(clientes , is(not(nullValue())));
        assertThat(clientes.size(), is(0));
    }
}