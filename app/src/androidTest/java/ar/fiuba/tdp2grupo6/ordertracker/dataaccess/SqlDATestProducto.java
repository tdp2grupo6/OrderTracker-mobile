package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.db.DbHelper;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SqlDATestProducto {

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
    public void testProductosGuardar() throws Exception {
        //SqlDA mDataBase = new SqlDA(mMockContext);

        //graba
        Producto producto =  new Producto();
        producto.id = 1;
        producto.nombre = "Un Producto";
        producto.marca = "Marca TM";
        producto.caracteristicas = "Caracteristicas del Producto";
        producto.precio = 49.99;
        producto.stock = 150;
        mDataBase.productoGuardar(producto);

        //busca
        ArrayList<Producto> productos = mDataBase.productoBuscar(producto.id);

        assertThat(productos , is(not(nullValue())));
        assertThat(productos.size(), is(1));
        assertThat(productos.get(0).id, is(producto.id));
        assertThat(productos.get(0).nombre, is(producto.nombre));
        assertThat(productos.get(0).marca, is(producto.marca));
        assertThat(productos.get(0).caracteristicas, is(producto.caracteristicas));
        assertThat(productos.get(0).precio, is(producto.precio));
        assertThat(productos.get(0).stock, is(producto.stock));
    }

    @Test
    public void testProductosActualizar() throws Exception {
        //SqlDA mDataBase = new SqlDA(mMockContext);

        //graba
        Producto producto =  new Producto();
        producto.id = 1;
        producto.nombre = "Un Producto";
        producto.marca = "Marca TM";
        producto.caracteristicas = "Caracteristicas del Producto";
        producto.precio = 49.99;
        producto.stock = 150;
        mDataBase.productoGuardar(producto);

        //actualiza
        producto.id = 1;
        producto.nombre = "Un Producto v2.0";
        producto.marca = "Marca TM Evolved";
        producto.caracteristicas = "Caracteristicas del Producto v2.0";
        producto.precio = 39.99;
        producto.stock = 100;
        long cant = mDataBase.productoActualizar(producto);

        //busca
        ArrayList<Producto> productos = mDataBase.productoBuscar(producto.id);

        assertThat(cant, is((long)1));
        assertThat(productos, is(not(nullValue())));
        assertThat(productos.size(), is(1));
        assertThat(productos.get(0).id, is(producto.id));
        assertThat(productos.get(0).nombre, is(producto.nombre));
        assertThat(productos.get(0).marca, is(producto.marca));
        assertThat(productos.get(0).caracteristicas, is(producto.caracteristicas));
        assertThat(productos.get(0).precio, is(producto.precio));
        assertThat(productos.get(0).stock, is(producto.stock));
    }

    @Test
    public void testProductosVaciar() throws Exception {
        //SqlDA mDataBase = new SqlDA(mMockContext);

        Producto producto =  new Producto();
        producto.id = 1;
        producto.nombre = "Un Producto";
        producto.caracteristicas = "Caracteristicas del Producto";
        producto.precio = 49.99;
        producto.stock = 150;

        mDataBase.productoGuardar(producto);
        mDataBase.productoVaciar();
        ArrayList<Producto> productos = mDataBase.productoBuscar(producto.id);

        assertThat(productos , is(not(nullValue())));
        assertThat(productos.size(), is(0));
    }

}