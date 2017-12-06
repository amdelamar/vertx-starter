package hello;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;

public class MainVerticle extends AbstractVerticle {

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
        httpServer.listen(8080, handler -> {
            if (handler.succeeded()) {
                System.out.println("Server started on http://localhost:8080/");
            } else {
                System.err.println("Server failed to start on port 8080. Is it in use?");
            }
            fut.complete();
        });
    }

    @Override
    public void stop(Future fut) {
        httpServer.close(handler -> {
            System.out.println("Server stopped.");
            fut.complete();
        });
    }
}
