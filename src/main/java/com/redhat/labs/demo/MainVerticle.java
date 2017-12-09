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
        router.route().handler(ctx -> {
            ctx.response()
                    // do not allow proxies to cache the data
                    .putHeader("Cache-Control", "no-store, no-cache")
                    // prevents Internet Explorer from MIME - sniffing a
                    // response away from the declared content-type
                    .putHeader("X-Content-Type-Options", "nosniff")
                    // Strict HTTPS (for about ~6Months)
                    .putHeader("Strict-Transport-Security", "max-age=" + 15768000)
                    // IE8+ do not allow opening of attachments in the context of this resource
                    .putHeader("X-Download-Options", "noopen")
                    // enable XSS for IE
                    .putHeader("X-XSS-Protection", "1; mode=block")
                    // deny frames
                    .putHeader("X-FRAME-OPTIONS", "DENY");
        });

        router.get("/rest/v1/hello").handler(this::helloHandler);

        router.route().handler(StaticHandler.create("webroot"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, res -> handleServerStart(startFuture, res));
    }

    void helloHandler(RoutingContext ctx) {
        String message = ctx.request().getParam("name");
        ctx.response()
                .setStatusCode(OK.code())
                .setStatusMessage(OK.reasonPhrase())
                .end(String.format("Hello %s!!!", message));
    }

    void handleServerStart(Future<Void> startFuture, AsyncResult<HttpServer> res) {
        if (res.succeeded()) {
            startFuture.complete();
        } else {
            startFuture.fail(res.cause());
        }
    }
}
