package com.smartcampus.resources;


import com.smartcampus.models.Room;
import com.smartcampus.service.DataService;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    DataService dataService = DataService.getInstance();

    @GET
    public List<Room> getRooms() {
        return dataService.getRooms();
    }

    // 2. GET ROOM BY ID 
    @GET
    @Path("/{id}")
    public Response getRoom(@PathParam("id") String id) {
        Room room = dataService.getRooms().stream()
                .filter(r -> Objects.equals(r.getId(), id))
                .findFirst()
                .orElse(null);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    // 3. POST (ADD) ROOM 
    @POST
    public Response addRoom(Room room, @Context UriInfo uriInfo) {
        // Check for duplicates (Testing Step 9)
        boolean exists = dataService.getRooms().stream()
                .anyMatch(r -> Objects.equals(r.getId(), room.getId()));
        
        if (exists) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room already exists");
            return Response.status(Response.Status.CONFLICT)
                    .entity(error)
                    .build();
        }

        dataService.getRooms().add(room);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(room.getId()).build())
                .entity(room)
                .build();
    }

    // 4. DELETE ROOM 
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") String id) {
        // Step 16: Prevent delete if sensors are attached
        boolean hasSensors = dataService.getSensors().stream()
                .anyMatch(s -> Objects.equals(s.getRoomId(), id));

        if (hasSensors) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Cannot delete room with active sensors");
            return Response.status(Response.Status.CONFLICT)
                    .entity(error)
                    .build();
        }

        dataService.getRooms().removeIf(r -> Objects.equals(r.getId(), id));
        return Response.noContent().build();
    }
}