package service.endpoint;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("/")
public class RoomResources {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {

        return "world";
    }
}
