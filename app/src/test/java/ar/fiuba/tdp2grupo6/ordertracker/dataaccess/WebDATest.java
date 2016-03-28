package ar.fiuba.tdp2grupo6.ordertracker.dataaccess;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


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
        assertThat("/cliente", is(req.getPath()));
        assertThat("GET", is(req.getMethod()));
        assertThat(response , is(not(nullValue())));
        assertThat("myString", is(response.getData()));
    }
}