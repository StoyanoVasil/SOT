package service.endpoint;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    private JWTVerifier verifier;

    public RoomResources() {
        rooms = new ArrayList<>();
        this.verifier = JWT.require(Algorithm.HMAC256("rest_sot_assignment")).build();
        rooms.add(new Room("test", "test", "alomin", 450));
        rooms.add(new Room("tset", "test", "alomin", 450));
    }

    private boolean isAdmin(String token) {

        String permission = decodeToken(token).getSubject();
        return permission.equals("admin");
    }

    private boolean isLandlord(String token) {
        String permission = decodeToken(token).getSubject();
        return permission.equals("admin") || permission.equals("landlord");
    }

    private DecodedJWT decodeToken(String token) {
        return verifier.verify(token);
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
    public Response getAllRooms(@HeaderParam("Authorization") String token) {

        try {
            if (isAdmin(token)) {
                return Response.status(200).entity(this.rooms).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("free")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFreeRooms(@HeaderParam("Authorization") String token) {

        try{
            decodeToken(token);
            List<Room> rms = new ArrayList<>();
            for (Room room : this.rooms) {
                if (room.getStatus().equals("free")) { rms.add(room); }
            }
            if (rms.size() > 0) {
                return Response.status(200).entity(rms).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.status(404).entity("No free rooms").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            decodeToken(token);
            Room room = roomExists(id);
            if (room != null) {
                return Response.status(200).entity(room).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.status(404).entity("Room not found").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("rooms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomsByCity(@QueryParam("city") String city, @HeaderParam("Authorization") String token) {

        try {
            decodeToken(token);
            List<Room> rms = new ArrayList<>();
            for (Room room : this.rooms) {
                if (room.getCity().equals(city)) {
                    rms.add(room);
                }
            }
            if (rms.size() > 0) {
                return Response.status(200).entity(rms).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.status(404).entity("No rooms in that city").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("rooms/landlord/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomsByLandlord(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if (isLandlord(token)) {
                List<Room> rms = new ArrayList<>();
                for (Room room : this.rooms) {
                    if (room.getLandlord().equals(id)) {
                        rms.add(room);
                    }
                }
                if (rms.size() > 0) {
                    return Response.status(200).entity(rms).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(404).entity("No rooms for that landlord!").type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @POST
    @Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newRoom(Room room, @HeaderParam("Authorization") String token) {

        try {
            if (isLandlord(token)) {
                if (roomExists(room.getAddress()) == null) {
                    rooms.add(room);
                    return Response.status(201).entity("Room posted!").type(MediaType.TEXT_PLAIN).build();
                }
                return Response.status(409).entity("Room with that address is already posted!")
                        .type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/{id}/book")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            decodeToken(token);
            Room room = roomExists(id);
            if (room != null) {
                room.book();
                return Response.status(204).build();
            }
            return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/{id}/rent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rentRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if(isLandlord(token)) {
                Room room = roomExists(id);
                if (room != null) {
                    room.rent();
                    return Response.status(204).build();
                }
                return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @DELETE
    @Path("room/{id}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if (isLandlord(token)) {
                Room room = roomExists(id);
                if (room != null) {
                    this.rooms.remove(room);
                    return Response.status(204).build();
                }
                return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }
}
