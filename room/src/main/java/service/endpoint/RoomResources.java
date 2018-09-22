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
        rooms.add(new Room("test", "test", "alomin", 450));
        rooms.add(new Room("tset", "test", "alomin", 450));
    }

    private Room roomExists(String id) {

        for (Room room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {

        //TODO: check if admin
        return Response.status(200).entity(this.rooms).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("room/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("id") String id) {

        //TODO: check if authenticated
        Room room = roomExists(id);
        if (room != null) {
            return Response.status(200).entity(room).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(404).entity("Room not found").type(MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("rooms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomsByCity(@QueryParam("city") String city) {

        //TODO: check if authenticated
        List<Room> rms = new ArrayList<>();
        for (Room room : this.rooms) {
            if (room.getCity().equals(city)) { rms.add(room); }
        }
        if (rms.size() > 0) {
            return Response.status(200).entity(rms).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(404).entity("No rooms in that city").type(MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("rooms/landlord/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomsByLandlord(@PathParam("id") String id) {

        //TODO: check if authenticated
        List<Room> rms = new ArrayList<>();
        for (Room room : this.rooms) {
            if (room.getLandlord().equals(id)) { rms.add(room); }
        }
        if (rms.size() > 0) {
            return Response.status(200).entity(rms).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.status(404).entity("No rooms for that landlord!").type(MediaType.TEXT_PLAIN).build();
    }


    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newRoom(Room room) {

        if(roomExists(room.getAddress()) == null) {
            rooms.add(room);
            return Response.status(201).entity("Room posted!").type(MediaType.TEXT_PLAIN).build();
        }
        return Response.status(409).entity("Room with that address is already posted!")
                .type(MediaType.TEXT_PLAIN).build();
    }

    @PUT
    @Path("room/{id}/book")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookRoom(@PathParam("id") String id) {

        //TODO: check if authenticated student
        Room room = roomExists(id);
        if (room != null) {
            room.book();
            return Response.status(204).build();
        }
        return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
    }

    @PUT
    @Path("room/{id}/rent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentRoom(@PathParam("id") String id) {

        //TODO: check if authenticated landlord
        Room room = roomExists(id);
        if (room != null) {
            room.rent();
            return Response.status(204).build();
        }
        return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
    }

    @DELETE
    @Path("room/{id}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id) {

        //TODO: check if admin or landlord(owner of the room)
        Room room = roomExists(id);
        if (room != null) {
            this.rooms.remove(room);
            return Response.status(204).build();
        }
        return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
    }
}
