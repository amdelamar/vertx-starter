package hello;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpVersion;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class HelloHandlerTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    private Vertx vertx;

    @Before
    public void setUp(TestContext test) {
        vertx = rule.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), test.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext test) {
        vertx.close(test.asyncAssertSuccess());
    }

    @Test(timeout = 1000l)
    public void testRedirect(TestContext test) {
        Async async = test.async();

        HttpClientOptions options = new HttpClientOptions().setProtocolVersion(HttpVersion.HTTP_1_1)
                .setSsl(true)
                .setVerifyHost(false)
                .setTrustAll(true);

        vertx.createHttpClient(options)
                .getNow(8443, "localhost", "/hello", response -> {
                    test.assertEquals(response.statusCode(), 200);
                    response.bodyHandler(body -> {
                        test.assertTrue(body.length() > 0);
                        async.complete();
                    });
                    async.complete();
                });
    }

}
