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
        rooms.add(new Room("d7947ed9-9e39-49ec-9930-4a6a24c46105","Drijffhoutstraat 27", "Eindhoven", "7f8365a9-2409-4bee-ac92-b874eeacf159", 400));
        rooms.add(new Room("ff26d1d1-3ce5-4694-b564-6eb7ec391090","Anjeliersstraat 118", "Amsterdam", "7f8365a9-2409-4bee-ac92-b874eeacf159", 750));
        rooms.add(new Room("b085ba9a-d4c1-4575-a579-d90378910642","Paul Citroenstraat 13", "Utrecht", "b26c04c2-cd5b-4337-a6ed-cb7bcfe790a5", 600));
        rooms.add(new Room("22122569-a73d-437a-8b84-af3b22532c23","Jan van Goyenstraat 28", "Eindhoven", "b26c04c2-cd5b-4337-a6ed-cb7bcfe790a5", 500));
        rooms.add(new Room("5b9f33c7-cb90-46e3-b420-3bed4795ba54","Pijlstaartvlinder 22", "Utrecht", "5ab557a7-98bd-45a9-b6ba-7a8173fd64c3", 850));
        rooms.add(new Room("de8ba56c-66bb-49e5-8a20-3e8c98a6bd10","1e Delistraat 11", "Utrecht", "5ab557a7-98bd-45a9-b6ba-7a8173fd64c3", 900));
        rooms.add(new Room("5f90965d-2a2d-48ae-b037-0eed7b9b838d","Potgieterstraat 33", "Amsterdam", "5ab557a7-98bd-45a9-b6ba-7a8173fd64c3", 1050));
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

        for (Room room : this.rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    private Room roomExistsAddress(String address) {

        for (Room room : this.rooms) {
            if (room.getAddress().equals(address)) {
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
                if (roomExistsAddress(room.getAddress()) == null) {
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
            DecodedJWT tkn = decodeToken(token);
            Room room = roomExists(id);
            if (room != null) {
                room.book(tkn.getKeyId());
                return Response.status(204).build();
            }
            return Response.status(404).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("room/{id}/book/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            DecodedJWT jwt = decodeToken(token);
            Room room = roomExists(id);
            String tknId = jwt.getKeyId();
            if (room != null) {
                if (room.getLandlord().equals(tknId) || room.getTenant().equals(tknId)) {
                    room.cancelBooking();
                    return Response.status(204).build();
                }
                return Response.status(401).build();
            }
            return Response.status(400).entity("Room not found!").type(MediaType.TEXT_PLAIN).build();
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

    @DELETE
    @Path("rooms/{id}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoomsByLandlord(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if (isAdmin(token)) {
                List<Room> rms = new ArrayList<>();
                for (Room room : this.rooms) {
                    if (!room.getLandlord().equals(id)) {
                        rms.add(room);
                    }
                }
                this.rooms = rms;
                return Response.status(204).build();
            }
            return Response.status(401).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("rooms/tenant/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomsByTenant(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            decodeToken(token);
            List<Room> rms = new ArrayList<>();
            for (Room room : this.rooms) {
                if(room.getTenant().equals(id)) {
                    rms.add(room);
                }
            }
            if (rms.size() > 0) {
                return Response.status(200).entity(rms).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.status(404).entity("No rooms found for this tenant!").type(MediaType.TEXT_PLAIN).build();
        } catch (JWTVerificationException e) {
            return Response.status(401).build();
        }
    }

    @GET
    @Path("rooms/{id}/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRooms(@PathParam("id") String id, @HeaderParam("Authorization") String token) {

        try {
            if (isAdmin(token)) {
                for (Room room : this.rooms) {
                    if (room.getTenant().equals(id)) {
                        room.cancelBooking();
                    }
                }
                return Response.status(201).build();
            }
            return Response.status(401).build();
        } catch (JWTDecodeException e) {
            return Response.status(401).build();
        }
    }
}
