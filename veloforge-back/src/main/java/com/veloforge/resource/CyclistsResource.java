package com.veloforge.resource;

import com.veloforge.api.CyclistsApi;
import com.veloforge.model.CreateCyclistRequest;
import com.veloforge.model.Cyclist;
import com.veloforge.service.CyclistService;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.List;

public class CyclistsResource implements CyclistsApi {
    
    @Inject
    CyclistService cyclistService;
    
    @Override
    public Response createCyclist(CreateCyclistRequest createCyclistRequest) {
        try {
            Cyclist cyclist = cyclistService.createCyclist(createCyclistRequest);
            return Response.status(Response.Status.CREATED).entity(cyclist).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @Override
    public Response getCyclist(String cyclistId) {
        return cyclistService.getCyclistById(cyclistId)
                .map(cyclist -> Response.ok(cyclist).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @Override
    public Response listCyclists() {
        List<Cyclist> cyclists = cyclistService.getAllCyclists();
        return Response.ok(cyclists).build();
    }
}