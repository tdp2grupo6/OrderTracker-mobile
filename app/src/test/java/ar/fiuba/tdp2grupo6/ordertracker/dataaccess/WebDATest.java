package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


import static junit.framework.TestCase.fail;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WebDATest {

    private MockWebServer mServer;
    private WebDA mServiceDA;

    @Mock
    Context mMockContext;

    @Before
    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServiceDA = new WebDA(mMockContext, mServer.url("/").toString(),30000, 120000);
    }

    @After
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    @Test
    public void testGetClientes() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody("myString"));
        ResponseObject response = mServiceDA.getClientes();

        RecordedRequest req = mServer.takeRequest();
        assertThat(req.getPath(), is("/cliente"));
        assertThat(req.getMethod(), is("GET"));
        assertThat(response, is(not(nullValue())));
        assertThat(response.getHasError(), is(false));
        assertThat(response.getData(), is("myString"));
        assertThat(response.getError(), is(nullValue()));
    }

    @Test
    public void testGetClientesError() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(500).setBody(""));
        try {
            ResponseObject response = mServiceDA.getClientes();
            fail("Expected an ServiceException to be thrown");
        } catch (ServiceException se) {
            assertThat(se.getMessage(), is(""));
            assertThat(se.isInternalError(), is(true));

            RecordedRequest req = mServer.takeRequest();
            assertThat(req.getPath(), is("/cliente"));
            assertThat(req.getMethod(), is("GET"));
            //assertThat(response, is(not(nullValue())));
            //assertThat(response.getHasError(), is(true));
            //assertThat(response.getData(), is(nullValue()));
            //assertThat(response.getError(), is(not(nullValue())));
        }
    }
}