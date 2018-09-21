package service.endpoint;

import service.models.Room;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Path("/")
public class RoomResources {

    private List<Room> rooms;

    public RoomResources() {
        rooms = new ArrayList<>();
    }

    private Room roomExists(String address) {

        for (Room room : rooms) {
            if (room.getAddress().equals(address)) {
                return room;
            }
        }
        return null;
    }

    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newRoom(Room room) {

        if(roomExists(room.getAddress()) == null) {
            rooms.add(room);
            return Response.status(201).entity("Room posted!").type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(409).entity("Room with that address is already posted!")
                .type(MediaType.TEXT_PLAIN).build();
    }
}
