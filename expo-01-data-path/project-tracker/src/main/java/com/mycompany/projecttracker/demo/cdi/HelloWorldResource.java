package com.mycompany.projecttracker.demo.cdi;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

/**
 * REST resource used to demonstrate CDI injection with qualified greeting services.
 */
@Path("hello")
public class HelloWorldResource {

    /**
     * Default greeting service selected with the {@code default} qualifier value.
     */
    @Inject
    @GreetingType("default")
    GreetingService greetingService;

    /**
     * Mock greeting service selected with the {@code mock} qualifier value.
     */
    @Inject
    @GreetingType("mock")
    GreetingService greetingMockService;

    /**
     * Returns a greeting from the default service.
     *
     * @param name optional name query parameter
     * @return HTTP response with the greeting text
     */
    @GET
    public Response hello(@QueryParam("name") @DefaultValue("world") String name) {
        return Response
            .ok(greetingService.greet(name))
            .build();
    }

    /**
     * Returns a greeting from the mock service.
     *
     * @param name optional name query parameter
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
