package com.redhat.labs.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);

        router.get("/rest/v1/hello").handler(this::helloHandler);

        router.route().handler(StaticHandler.create("webroot"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, res -> handleServerStart(startFuture, res));
    }

    private void helloHandler(RoutingContext ctx) {
        String message = ctx.request().getParam("name");
        ctx.response()
                .setStatusCode(OK.code())
                .setStatusMessage(OK.reasonPhrase())
                .end(String.format("Hello %s!!!", message));
    }

    private void handleServerStart(Future<Void> startFuture, AsyncResult<HttpServer> res) {
        if (res.succeeded()) {
            startFuture.complete();
        } else {
            startFuture.fail(res.cause());
        }
    }
}
