
package upcase;

import java.util.Collections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A simple JAX-RS resource to upper-case a single path parameter.
 *
 * The message is returned as a JSON object.
 */
@Path("/upcase")
@RequestScoped
public class UpcaseResource {

    private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

    private final DelayMSProvider delayMSProvider;

    @Inject
    public UpcaseResource(DelayMSProvider delayMSProvider) {
        this.delayMSProvider = delayMSProvider;
    }

    /**
     * Return a worldly greeting message.
     *
     * @return {@link JsonObject}
     */
    @Path("/{value}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject upcase(@PathParam("value") String value) throws InterruptedException {
        long delayToUse = delayMSProvider.get();
        if (delayToUse > 0) {
            Thread.sleep(delayToUse);
        }
        return JSON.createObjectBuilder()
                .add("value", value.toUpperCase())
                .build();
    }

    /**
     * Change the delay time.
     *
     * @param newDelay JSON object containing "newDelay" setting
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setDelay(JsonObject newDelay) {
        delayMSProvider.set(newDelay.getJsonNumber("delay").longValue());
        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject delay() {
        return JSON.createObjectBuilder()
                .add("delay", delayMSProvider.get())
                .build();
    }
}
