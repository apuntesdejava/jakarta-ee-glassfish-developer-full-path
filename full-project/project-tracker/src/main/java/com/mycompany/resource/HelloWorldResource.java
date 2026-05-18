package com.mycompany.resource;

import com.mycompany.projecttracker.service.GreetingService;
import com.mycompany.projecttracker.service.qualifier.GreetingType;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

/**
 * REST resource that exposes greeting examples.
 */
@Path("hello")
public class HelloWorldResource {

    /** Default greeting service selected with CDI qualifiers. */
    @Inject
    @GreetingType("default")
    GreetingService greetingService;

    /** Mock greeting service selected with CDI qualifiers. */
    @Inject
    @GreetingType("mock")
    GreetingService greetingMockService;

    /**
     * Returns the default greeting.
     *
     * @param name optional caller name
     * @return HTTP response with the greeting text
     */
    @GET
    public Response hello(@QueryParam("name") @DefaultValue("world") String name) {
        return Response
            .ok(greetingService.greet(name))
            .build();
    }

    /**
     * Returns the mock greeting.
     *
     * @param name optional caller name
     * @return HTTP response with the mock greeting text
     */
    @GET
    @Path("mock")
    public Response helloMock(@QueryParam("name") @DefaultValue("world") String name) {
        return Response
            .ok(greetingMockService.greet(name))
            .build();
    }


}
