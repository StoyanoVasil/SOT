package service.endpoint;

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

    public UserResources() {
        this.users = new ArrayList<>();
        this.users.add(new User("admin@ad.min", "admin", "admin", "admin"));
    }

    private User userExists(String email) {

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

        if(userExists(user.getEmail()) == null) {
            this.users.add(user);
            return Response.status(201).entity(user.createToken()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(409).entity("User already exists!").type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("authenticate")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getToken(@FormParam("email") String email, @FormParam("password") String password) {

        User user = userExists(email);
        if (user != null){
            String token = user.authenticate(password);
            if (token != null) {
                return Response.status(201).entity(token).type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).entity("Login unsuccessful!").type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(401).entity("No user with that email!").type(MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {

        //TODO: check if admin
        return Response.status(200).entity(this.users).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") String id) {

        //TODO: check if admin
        for (User user : this.users) {
            if (user.getId().equals(id)) {
                return Response.status(200).entity(user).type(MediaType.APPLICATION_JSON).build();
            }
        }
        return Response.status(404).entity("User not found!").type(MediaType.TEXT_PLAIN).build();
    }

    @DELETE
    @Path("remove/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("id") String id) {

        //TODO: check if admin
        for (User user : this.users) {
            if (user.getId().equals(id)) {
                this.users.remove(user);
                return Response.status(204).build();
            }
        }
        return Response.status(404).entity("User not found!").type(MediaType.TEXT_PLAIN).build();
    }
}
