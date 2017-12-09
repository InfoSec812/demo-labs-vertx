package com.redhat.labs.demo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    Vertx vertx;
    private MainVerticle main;

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        main = new MainVerticle();
        vertx.deployVerticle(main, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testHelloEndpoint(TestContext context) {
        // Send a request and get a response
        HttpClient client = vertx.createHttpClient();
        Async async = context.async();
        client.getNow(8080, "localhost", "/rest/v1/hello?name=Test", resp -> {
            resp.bodyHandler(body -> {
                context.assertEquals("Hello Test!!!", body.toString());
                client.close();
                async.complete();
            });
        });
    }

    @Test
    public void testServerStartHandler() {
        Future f = Future.future();
        AsyncResult<HttpServer> res = mock(AsyncResult.class);
        when(res.succeeded()).thenReturn(Boolean.FALSE);
        main.handleServerStart(f, res);
        assertFalse("When the server start result is failed, suceeded MUST be false.", f.succeeded());
    }
}