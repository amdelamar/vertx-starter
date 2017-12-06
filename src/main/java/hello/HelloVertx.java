package hello;

import io.vertx.core.Vertx;

public class HelloVertx {

    /**
     * Simplest "Hello World!" for Vert.X
     */
    public static void main(String[] args) {
        Vertx.vertx()
                .createHttpServer()
                .requestHandler(req -> req.response()
                        .putHeader("Content-Type", "text/html; charset=utf-8")
                        .end("Hello from Vert.X!"))
                .listen(8080, handler -> {
                    if (handler.succeeded()) {
                        System.out.println("Server started on http://localhost:8080/");
                    } else {
                        System.err.println("Server failed to start on port 8080. Is it in use?");
                    }
                });
    }

}
