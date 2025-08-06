package com.veloforge.resource;

import com.veloforge.api.EventsApi;
import com.veloforge.model.*;
import com.veloforge.service.EventService;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.List;

public class EventsResource implements EventsApi {
    
    @Inject
    EventService eventService;
    
    @Override
    public Response addParticipant(String eventId, AddParticipantRequest addParticipantRequest) {
        return eventService.addParticipant(eventId, addParticipantRequest)
                .map(participant -> Response.ok(participant).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }
    
    @Override
    public Response createEvent(CreateEventRequest createEventRequest) {
        try {
            Event event = eventService.createEvent(createEventRequest);
            return Response.status(Response.Status.CREATED).entity(event).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @Override
    public Response getEvent(String eventId) {
        return eventService.getEventById(eventId)
                .map(event -> Response.ok(event).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
    
    @Override
    public Response getLeaderboard(String eventId) {
        try {
            List<LeaderboardEntry> leaderboard = eventService.getLeaderboard(eventId);
            return Response.ok(leaderboard).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    public Response listEvents() {
        List<Event> events = eventService.getAllEvents();
        return Response.ok(events).build();
    }
    
    @Override
    public Response startEvent(String eventId) {
        return eventService.startEvent(eventId)
                .map(event -> Response.ok(event).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }
}