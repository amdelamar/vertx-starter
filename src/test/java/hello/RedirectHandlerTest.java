package hello;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class RedirectHandlerTest {
    
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
        vertx.createHttpClient()
                .getNow(8080, "localhost", "/", response -> {
                    test.assertEquals(response.statusCode(), 302);
                    test.assertNotNull(response.getHeader("Location"));
                    async.complete();
                });
    }

}
