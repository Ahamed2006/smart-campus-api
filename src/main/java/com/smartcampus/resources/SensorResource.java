package com.smartcampus.resources;

import com.smartcampus.models.Sensor;
import com.smartcampus.service.DataService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    DataService dataService = DataService.getInstance();

    // 1. GET ALL 
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        if (type != null) {
            return dataService.getSensors().stream()
                    .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return dataService.getSensors();
    }

    // 2. GET BY ID 
    @GET
    @Path("/{id}")
    public Response getSensorById(@PathParam("id") String id) {
        Sensor sensor = dataService.getSensors().stream()
                .filter(s -> Objects.equals(s.getId(), id))
                .findFirst()
                .orElse(null);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(sensor).build();
    }

    // 3. POST - ADD SENSOR 
    @POST
    public Response addSensor(Sensor sensor) {
        // Step 10: Check if the Room exists
        boolean roomExists = dataService.getRooms().stream()
                .anyMatch(r -> Objects.equals(r.getId(), sensor.getRoomId()));

        if (!roomExists) {
            // Return 422 Unprocessable Entity if room doesn't exist
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room ID " + sensor.getRoomId() + " does not exist");
            return Response.status(422)
                    .entity(error)
                    .build();
        }

        // Check for duplicate sensor ID 
        boolean exists = dataService.getSensors().stream()
                .anyMatch(s -> Objects.equals(s.getId(), sensor.getId()));
        if (exists) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        dataService.getSensors().add(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // 4. SUB-RESOURCE LOCATOR 
    @Path("/{sensorId}/readings")
    public ReadingResource getReadingResource() {
        return new ReadingResource();
    }
}