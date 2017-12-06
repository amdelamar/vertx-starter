package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

public class VertxServer {

    public static final int HTTPPORT = 8080;
    public static final int HTTPSPORT = 8443;

    private static Vertx vertx;
    private static HttpServer httpServer;
    private static HttpServer httpsServer;

    public static void main(String[] args) {

        // Vertx
        vertx = Vertx.vertx();

        // Create HTTP server
        httpServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true));

        // Create HTTPS server
        httpsServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true)
                //.setUseAlpn(true) HTTP/2 not supported on this JDK
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPassword("changeit")
                        .setPath(System.getProperty("user.dir") + "/deploy/keystore.jks")));

        // Add Handlers
        httpsServer.requestHandler(request -> {
            System.out.println("Request for: "+request.uri());
            request.response()
                    .putHeader("Content-Type", "text/html; charset=utf-8")
                    .end("Hello from Vert.X! (HTTPS)");
        });
        
        // Redirect HTTP requests to HTTPS
        httpServer.requestHandler(request -> {
            System.out.println("Request for: "+request.uri());
            request.response()
                    .setStatusCode(302)
                    .putHeader("Content-Type", "text/html; charset=utf-8")
                    .putHeader("Location", "https://localhost:"+HTTPSPORT+request.uri())
                    .end("");
        });

        // Start listening
        httpServer.listen(HTTPPORT, handler -> {
            if (handler.succeeded()) {
                System.out.println("Listening on http://localhost:" + HTTPPORT);
            } else {
                System.err.println("Failed to bind on port " + HTTPPORT + ". Is it being used?");
            }
        });
        httpsServer.listen(HTTPSPORT, handler -> {
            if (handler.succeeded()) {
                System.out.println("Listening on https://localhost:" + HTTPSPORT);
            } else {
                System.err.println("Failed to bind on port " + HTTPSPORT + ". Is it being used?");
            }
        });
    }
}
