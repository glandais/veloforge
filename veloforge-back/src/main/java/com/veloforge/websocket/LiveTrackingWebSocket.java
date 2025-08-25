package com.veloforge.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veloforge.model.Position;
import com.veloforge.service.EventService;

@ServerEndpoint("/ws/events/{eventId}/positions")
@ApplicationScoped
public class LiveTrackingWebSocket {

  @Inject EventService eventService;

  @Inject ObjectMapper objectMapper;

  private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("eventId") String eventId) {
    sessions.put(session.getId(), session);
    session.getUserProperties().put("eventId", eventId);

    // Send initial positions
    try {
      List<Position> positions = eventService.getCurrentPositions(eventId);
      String message =
          objectMapper.writeValueAsString(Map.of("type", "positions", "data", positions));
      session.getAsyncRemote().sendText(message);
    } catch (Exception e) {
      // Log error
    }
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(session.getId());
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    sessions.remove(session.getId());
  }

  public static void broadcastPositions(
      String eventId, List<Position> positions, ObjectMapper objectMapper) {
    try {
      String message =
          objectMapper.writeValueAsString(Map.of("type", "positions", "data", positions));

      sessions.values().parallelStream()
          .filter(session -> eventId.equals(session.getUserProperties().get("eventId")))
          .forEach(
              session -> {
                try {
                  session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                  // Log error
                }
              });
    } catch (Exception e) {
      // Log error
    }
  }
}
