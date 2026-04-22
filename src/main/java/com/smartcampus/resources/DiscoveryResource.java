package com.smartcampus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/") 
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/rooms");
        links.put("sensors", "/api/sensors");

        return Response.ok(links).build();
    }
}