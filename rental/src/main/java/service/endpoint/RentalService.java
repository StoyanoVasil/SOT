package service.endpoint;

import org.glassfish.jersey.client.ClientConfig;
import service.models.*;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
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

    // unprotected routes
    //TODO: make checks and return appropriate messages
    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@FormParam("email") String email, @FormParam("name") String name,
                             @FormParam("role") String role, @FormParam("password") String password) {

        Builder reqBuilder1 = this.client.path("user/api/register")
                .request(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON);
        Response res1 = reqBuilder1.post(Entity.entity(new User(email, name, password, role),
                MediaType.APPLICATION_JSON));
        String hello = res1.readEntity(String.class);
        return Response.status(201).entity(hello).type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@FormParam("email") String email, @FormParam("password") String password) {

        Form form = new Form();
        form.param("email", email);
        form.param("password", password);
        Builder reqBuilder1 = this.client.path("user/api/authenticate").request(MediaType.TEXT_PLAIN);
        Response res1 = reqBuilder1.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        String hello = res1.readEntity(String.class);
        return Response.status(201).entity(hello).type(MediaType.TEXT_PLAIN).build();
    }

    //protected routes
    @POST
    @Path("new/room")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newRoom(@FormParam("address") String address,
                            @FormParam("rent") int rent) {

        //TODO: get landlord id from token
        Room room = new Room(address, "test", rent);
        Builder reqBuilder1 = this.client.path("room/api/new").request(MediaType.APPLICATION_JSON);
        return reqBuilder1.post(Entity.entity(room, MediaType.APPLICATION_JSON));
    }
}
