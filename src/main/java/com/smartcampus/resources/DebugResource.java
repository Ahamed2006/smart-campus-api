package com.smartcampus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/debug")
public class DebugResource {

    @GET
    @Path("/error")
    public Response triggerError() {
        throw new RuntimeException("Test error");
    }
}