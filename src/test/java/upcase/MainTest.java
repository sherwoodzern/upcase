
package upcase;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.spi.CDI;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.helidon.microprofile.server.Server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class MainTest {
    private static Server server;

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    @BeforeAll
    public static void startTheServer() throws Exception {
        server = Server.create().start();
    }

    @Test
    void testUpcase() {
        Client client = ClientBuilder.newClient();

        JsonObject jsonObject = client
                .target(getConnectionString("/upcase/Hello"))
                .request()
                .get(JsonObject.class);
        Assertions.assertEquals("HELLO", jsonObject.getString("value"),
                "default message");
   }

   @Test
   void testDelay() {
        Client client = ClientBuilder.newClient();

        JsonObject newDelay = JSON.createObjectBuilder()
                .add("delay", 500L)
                .build();
        String connectionString = getConnectionString("/upcase");
        client
                .target(connectionString)
                .request()
                .put(Entity.json(newDelay));

        JsonObject json = client
                .target(connectionString)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(JsonObject.class);

        long delay = json.getJsonNumber("delay").longValue();
        Assertions.assertEquals(500L, delay);
   }

    @AfterAll
    static void destroyClass() {
        CDI<Object> current = CDI.current();
        ((SeContainer) current).close();
    }

    private String getConnectionString(String path) {
        return "http://localhost:" + server.port() + path;
    }
}
