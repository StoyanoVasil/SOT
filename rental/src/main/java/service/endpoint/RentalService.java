package service.endpoint;

import org.glassfish.jersey.client.ClientConfig;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Singleton
@Path("/")
public class RentalService {

    private WebTarget client;

    public RentalService() {
        this.client = getClient();
    }

    private WebTarget getClient() {

        URI baseUri = UriBuilder.fromUri("http://localhost:8080/").build();
        Client client = ClientBuilder.newClient(new ClientConfig());
        return client.target(baseUri);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {

        Builder reqBuilder1 = this.client.path("user/api/").request().accept(MediaType.TEXT_PLAIN);
        Response res1 = reqBuilder1.get();
        String hello = res1.readEntity(String.class);

        Builder reqBuilder2 = this.client.path("room/api/").request().accept(MediaType.TEXT_PLAIN);
        Response res2 = reqBuilder2.get();
        String world = res2.readEntity(String.class);

        return hello + " " + world;
    }
}
