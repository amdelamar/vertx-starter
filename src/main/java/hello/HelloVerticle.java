package hello;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

public class HelloVerticle extends AbstractVerticle {

    private HttpServer httpServer;

    @Override
    public void start(Future fut) {
        // Create an HTTP server...
        httpServer = vertx.createHttpServer();

        // ... which simply returns hello to each request.
        httpServer.requestHandler(req -> {
            req.response()
                    .putHeader("Content-Type", "text/html; charset=utf-8")
                    .end("Hello from Vert.X!");
        });
        httpServer.listen(8080, res -> fut.complete());
    }

    @Override
    public void stop(Future fut) {
        httpServer.close(res -> fut.complete());
    }
}
