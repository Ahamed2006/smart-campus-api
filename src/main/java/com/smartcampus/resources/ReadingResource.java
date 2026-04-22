package com.smartcampus.resources;

import com.smartcampus.models.Reading;
import com.smartcampus.models.Sensor;
import com.smartcampus.service.DataService;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReadingResource {
    DataService dataService = DataService.getInstance();

    @GET
    public List<Reading> getReadings(@PathParam("sensorId") String sensorId) {
        return dataService.getReadings().stream()
                .filter(r -> r.getSensorId() != null && r.getSensorId().equals(sensorId))
                .collect(Collectors.toList());
    }

    @POST
    public Response addReading(@PathParam("sensorId") String sensorId, Reading reading) {
        Sensor s = dataService.getSensorById(sensorId);
        if (s == null) return Response.status(Response.Status.NOT_FOUND).build();
        
        // Maintenance check
        if ("MAINTENANCE".equalsIgnoreCase(s.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        reading.setSensorId(sensorId);
        s.setCurrentValue(reading.getValue()); 
        dataService.addReading(reading);
        
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}