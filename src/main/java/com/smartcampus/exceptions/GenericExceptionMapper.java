package com.smartcampus.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        
        if (exception instanceof javax.ws.rs.WebApplicationException) {
            return ((javax.ws.rs.WebApplicationException) exception).getResponse();
        }

       
        exception.printStackTrace();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"A server error occurred. Please try again later.\"}")
                .type("application/json")
                .build();
    }
}