package com.veloforge.resource;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import com.veloforge.api.PositionsApi;
import com.veloforge.model.Position;
import com.veloforge.service.EventService;

public class PositionsResource implements PositionsApi {

  @Inject EventService eventService;

  @Override
  public Response getCurrentPositions(String eventId) {
    try {
      List<Position> positions = eventService.getCurrentPositions(eventId);
      return Response.ok(positions).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
