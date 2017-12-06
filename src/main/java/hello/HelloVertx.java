package hello;

import io.vertx.core.Vertx;

public class HelloVertx {

    /**
     * Create an HTTP server which simply returns "Hello World!" to each request.
     */
  public static void main(String[] args) {
    Vertx.vertx()
      .createHttpServer()
      .requestHandler(req -> req.response().end("Hello World!"))
      .listen(8080, handler -> {
        if (handler.succeeded()) {
          System.out.println("Server started on http://localhost:8080/");
        } else {
          System.err.println("Server failed to start on port 8080. Is it in use?");
        }
      });
  }

}