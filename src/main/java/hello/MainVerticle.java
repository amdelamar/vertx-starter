package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

    public static final int HTTPPORT = 8080;
    public static final int HTTPSPORT = 8443;
    public static final String VERSION = "3.5.0";

    private static Vertx vertx;
    private static HttpServer httpServer;
    private static HttpServer httpsServer;
    private static Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        // Vertx core
        vertx = Vertx.vertx();

        // Verticle
        Verticle app = new MainVerticle();

        // Deploy Verticle
        vertx.deployVerticle(app, res -> {
            if (!res.succeeded()) {
                logger.error("FATAL: Deploy Verticle failed!");
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

        // Create HTTP server
        httpServer = getVertx().createHttpServer(new HttpServerOptions().setLogActivity(true));

        // Create HTTPS server
        httpsServer = getVertx().createHttpServer(new HttpServerOptions().setLogActivity(true)
                //.setUseAlpn(true) // HTTP/2 not supported on this JDK
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPassword("changeit")
                        .setPath(System.getProperty("user.dir") + "/deploy/keystore.jks")));

        // Map Routes
        Router mainRouter = Router.router(getVertx());
        mainRouter.route()
                .path("/hello")
                .handler(new HelloHandler());
        // Handle static resources
        mainRouter.route("/*").handler(StaticHandler.create());

        // Subrouter API
        Router apiRouter = Router.router(getVertx());
        apiRouter.route()
                .path("/api/*")
                .handler(new ApiHandler());
        mainRouter.mountSubRouter("/api", apiRouter);

        // Add Route Handler
        httpsServer.requestHandler(mainRouter::accept);

        // Redirect HTTP requests to HTTPS
        httpServer.requestHandler(new RedirectHandler());

        // Start listening
        httpServer.listen(HTTPPORT, handler -> {
            if (handler.succeeded()) {
                logger.info("Listening on port: " + HTTPPORT);
            } else {
                logger.error("Failed to bind on port " + HTTPPORT + ". Is it being used?");
            }
        });
        httpsServer.listen(HTTPSPORT, handler -> {
            if (handler.succeeded()) {
                logger.info("Listening on port: " + HTTPSPORT);
            } else {
                logger.error("Failed to bind on port " + HTTPSPORT + ". Is it being used?");
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
        logger.info("Stopped listening on ports: " + HTTPPORT + "/" + HTTPSPORT);
    }
}
