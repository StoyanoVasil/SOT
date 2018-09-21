package service.endpoint;

import service.models.User;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String register(User user) {

        if(userExists(user.getEmail()) == null) {
            this.users.add(user);
            return user.createToken();
        } else {
            return "User already exists!";
        }
    }

    @POST
    @Path("authenticate")
    @Produces(MediaType.TEXT_PLAIN)
    public String getToken(@FormParam("email") String email, @FormParam("password") String password) {

        User user = userExists(email);
        if (user != null){
            String token = user.authenticate(password);
            if (token != null) {
                return token;
            }
            return "Login unsuccessful!";
        }
        return "No user with that email!";
    }
}
