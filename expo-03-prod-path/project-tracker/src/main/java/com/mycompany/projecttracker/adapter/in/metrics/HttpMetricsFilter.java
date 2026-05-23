package com.mycompany.projecttracker.adapter.in.metrics;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@Priority(Priorities.USER)
public class HttpMetricsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String START_TIME = HttpMetricsFilter.class.getName() + ".startTime";

    @Inject
    private HttpMetricsRegistry registry;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object startTime = requestContext.getProperty(START_TIME);
        if (startTime instanceof Long startNanos) {
            registry.record(
                requestContext.getMethod(),
                path(requestContext),
                responseContext.getStatus(),
                System.nanoTime() - startNanos
            );
        }
    }

    private String path(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath(false);
        if (path == null || path.isBlank()) {
            return "/";
        }
        return "/" + path;
    }
}
