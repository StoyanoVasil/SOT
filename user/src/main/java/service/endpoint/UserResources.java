package service.endpoint;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;
import service.models.User;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/")
public class UserResources {

    private List<User> users;
    private JWTVerifier verifier;

    public UserResources() {
        this.users = new ArrayList<>();
        this.verifier = JWT.require(Algorithm.HMAC256("rest_sot_assignment")).build();
        this.users.add(new User("7f8365a9-2409-4bee-ac92-b874eeacf159", "admin@ad.min", "admin", "admin", "admin"));
    }

    private boolean isAdmin(String token) {

        String permission = decodeToken(token).getSubject();
        return permission.equals("admin");
    }

    private DecodedJWT decodeToken(String token) {
        return verifier.verify(token);
    }

    private User userExists(String id) {

        for (User user : users) {
            if (user.getId().equals(id)) { return user; }
        }
        return null;
    }

    private User userExistsEmail(String email) {

        for (User user : users) {
            if (user.getEmail().equals(email)) { return user; }
        }
        return null;
    }

    @POST
    @Path("register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(User user) {

        if(userExistsEmail(user.getEmail()) == null) {
            this.users.add(user);
            return Response.status(201).entity(user.createToken()).type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(409).entity("Email already in use!").type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@FormParam("email") String email, @FormParam("password") String password) {

        User user = userExistsEmail(email);
        if(user != null) {
            String token = user.authenticate(password);
            if (token != null) {
                return Response.status(200).entity(token).type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).entity("Login unsuccessful!").type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(404).entity("No user with that email!").type(MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("Authorization") String token) {

        try {
            if (isAdmin(token)) {
                return Response.status(200).entity(this.users).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            DecodedJWT jwt = decodeToken(token);
            User user = userExists(id);
            if (user != null) {
                if (isAdmin(token) || user.getId().equals(jwt.getKeyId())) {
                    return Response.status(200).entity(user).type(MediaType.APPLICATION_JSON).build();
                }
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @DELETE
    @Path("remove/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if (isAdmin(token)) {
                User user = userExists(id);
                if (user != null) {
                    this.users.remove(user);
                    return Response.status(204).build();
                }
                return Response.status(404).entity("User not found!").type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("role/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRole(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if (isAdmin(token)) {
                User user = userExists(id);
                if (user != null) {
                    return Response.status(200).entity(user.getRole()).type(MediaType.TEXT_PLAIN).build();
                }
                return Response.status(404).entity("User not found!").type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("name/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserName(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            decodeToken(token);
            User user = userExists(id);
            if (user != null) {
                return Response.status(200).entity(user.getName()).type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(404).entity("User not found!").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @PUT
    @Path("user/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(User user, @HeaderParam("Authorization") String token) {

        try {
            decodeToken(token);
            User usr = userExists(user.getId());
            this.users.remove(usr);
            this.users.add(user);
            return Response.status(204).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }
}
