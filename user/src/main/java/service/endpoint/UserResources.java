package service.endpoint;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/")
public class UserResources {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {

        return "Hello";
    }
}
