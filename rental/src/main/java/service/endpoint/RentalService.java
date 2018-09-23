package service.endpoint;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    private JWTVerifier verifier;

    public RentalService() {
        this.client = getClient();
        this.verifier = JWT.require(Algorithm.HMAC256("rest_sot_assignment")).build();
    }

    private WebTarget getClient() {

        URI baseUri = UriBuilder.fromUri("http://localhost:8080/").build();
        Client client = ClientBuilder.newClient(new ClientConfig());
        return client.target(baseUri);
    }

    private DecodedJWT verifyToken(String token) {

        return verifier.verify(token);
    }

    private String getTokenId(String token) {

        return verifyToken(token).getKeyId(); }

    // unprotected routes
    @POST
    @Path("new/user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@FormParam("email") String email, @FormParam("name") String name,
                             @FormParam("role") String role, @FormParam("password") String password) {

        //TODO: check if role is either landlord or student
        Builder reqBuilder1 = this.client.path("user/api/register")
                .request(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON);
        return reqBuilder1.post(Entity.entity(new User(email, name, password, role),
                MediaType.APPLICATION_JSON));
    }

    @POST
    @Path("user/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@FormParam("email") String email, @FormParam("password") String password) {

        Form form = new Form();
        form.param("email", email);
        form.param("password", password);
        Builder reqBuilder1 = this.client.path("user/api/authenticate").request(MediaType.TEXT_PLAIN);
        return reqBuilder1.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    }

    //protected routes
    @GET
    @Path("user/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("user/api/all")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).entity(e).build();
        }
    }

    @GET
    @Path("user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("user/api/user/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @DELETE
    @Path("remove/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            //get role from user service
            Builder reqBuilder = this.client
                    .path("role/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            Response r = reqBuilder.delete();

            //if landlord delete all rooms
            String role = r.readEntity(String.class);
            if (role.equals("landlord")) {
                Builder req = this.client
                        .path("rooms/" + id + "/delete")
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", token);
                req.delete();
            }

            //delete user
            Builder reqBuilder1 = this.client
                    .path("user/api/remove/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.delete();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms(@HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/all")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/free")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFreeRooms(@HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/free")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/room/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/city")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomByCity(@QueryParam("city") String city, @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/rooms")
                    .queryParam("city", city)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/landlord")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomsByLandlord(@HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            String id = getTokenId(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/rooms/landlord/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @POST
    @Path("new/room")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newRoom(@FormParam("address") String address,
                            @FormParam("city") String city,
                            @FormParam("rent") int rent,
                            @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            String id = getTokenId(token);
            Room room = new Room(address, city, id, rent);
            Builder reqBuilder1 = this.client
                    .path("room/api/new")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.post(Entity.entity(room, MediaType.APPLICATION_JSON));
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("book/room/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/room/" + id + "/book")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("rent/room/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/room/" + id + "/rent")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.get();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @DELETE
    @Path("delete/room/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {


        try {
            verifyToken(token);
            Builder reqBuilder1 = this.client
                    .path("room/api/room/" + id + "/delete")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", token);
            return reqBuilder1.delete();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }
}
