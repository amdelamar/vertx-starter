package hello;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

    public static int HTTPPORT = 8080;
    public static int HTTPSPORT = 8443;
    public static final String VERSION = "3.5.0";
    public static final String KEYSTORE = "/deploy/keystore.jks";
    public static final String KEYSTORE_PASSWORD = "changeit";

    private static HttpServer httpServer;
    private static HttpServer httpsServer;
    private static Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        // Vertx core
        Vertx vertx = Vertx.vertx();

        // Deploy Verticle        
        Single<String> deployment = vertx.rxDeployVerticle(MainVerticle.class.getName());

        // Observe deploy
        deployment.subscribe(s -> {
            logger.info("MainVerticle deployed: " + s);
        }, e -> {
            logger.error("FATAL: Deploy Verticle failed! ", e);
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // Check if necessary env/files are present
        Future<Void> validate = isValid();
        if (validate.failed()) {
            startFuture.fail(validate.cause());
            vertx.close();
            System.exit(1);
        }

        // Create HTTP server
        httpServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true));

        // Create HTTPS server
        httpsServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true)
                .setUseAlpn(true) // HTTP/2 only supported on JDK 9
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions().setPassword(KEYSTORE_PASSWORD)
                        .setPath(System.getProperty("user.dir") + KEYSTORE)));

        // Map Routes
        Router mainRouter = Router.router(vertx);
        mainRouter.get()
                .path("/hello")
                .handler(new HelloHandler());

        // Webroot resources
        mainRouter.route("/*")
                .handler(StaticHandler.create()
                        .setFilesReadOnly(false)
                        .setCachingEnabled(!true));
        // Readonly + nocache, so any changes in webroot are visible on browser refresh
        // for production, these wouldn't be needed.

        // Templating
        mainRouter.get()
                .path("/template")
                .handler(new TemplateHandler());

        // Add Subrouter api
        Router apiRouter = Router.router(vertx);
        apiRouter.get()
                .path("/")
                .handler(new ApiHandler());
        mainRouter.mountSubRouter("/api", apiRouter);

        // Set Router
        httpsServer.requestHandler(mainRouter::accept);

        // Redirect HTTP requests to HTTPS
        httpServer.requestHandler(new RedirectHandler());

        // Start listening
        httpServer.listen(HTTPPORT, asyncResult -> {
            if (asyncResult.succeeded()) {
                logger.info("Listening on port: " + HTTPPORT);
            } else {
                logger.error("Failed to bind on port " + HTTPPORT + ". Is it being used?");
                startFuture.fail(asyncResult.cause());
            }
        });
        httpsServer.listen(HTTPSPORT, asyncResult -> {
            if (asyncResult.succeeded()) {
                logger.info("Listening on port: " + HTTPSPORT);
                startFuture.complete();
            } else {
                logger.error("Failed to bind on port " + HTTPSPORT + ". Is it being used?");
                startFuture.fail(asyncResult.cause());
            }
        });
    }

    /**
     * Check if this application has access to the necessary files
     * and resources it needs. Like keystore and webroot.
     * @return Future (use Future.failed() to check if false)
     */
    private Future<Void> isValid() {
        Future<Void> future = Future.future();

        boolean flag = true;

        // PORT env check
        try {
            HTTPPORT = Integer.parseInt(System.getenv("PORT"));
        } catch (Exception e) {
            logger.warn("Environment variable PORT not found or not valid. Defautling to: " + HTTPPORT);
        }

        String dir = System.getProperty("user.dir");

        // SSL Keystore check
        File keystore = new File(dir + KEYSTORE);
        if (!keystore.exists() || !keystore.canRead()) {
            logger.error("Keystore file not found or can't read. Expected it here: " + keystore.getAbsolutePath());
            flag = false;
        }

        File webroot = new File(dir + "/webroot");
        if (!webroot.exists() || !webroot.isDirectory()) {
            logger.error("/webroot/ not found or can't read. Expected it here: " + dir);
            flag = false;
        }

        if (flag) {
            future.complete();
        } else {
            future.fail("App invalid config.");
        }

        return future;
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        logger.info("Stopped listening on ports: " + HTTPPORT + "/" + HTTPSPORT);
        stopFuture.complete();
    }
}
