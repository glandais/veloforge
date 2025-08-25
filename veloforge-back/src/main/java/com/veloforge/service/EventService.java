package com.veloforge.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.veloforge.entity.EventEntity;
import com.veloforge.entity.PositionEntity;
import com.veloforge.model.*;

@ApplicationScoped
public class EventService {

  @Inject SimulationService simulationService;

  public List<Event> getAllEvents() {
    return EventEntity.<EventEntity>listAll().stream().map(this::toModel).toList();
  }

  public Optional<Event> getEventById(String id) {
    EventEntity entity = EventEntity.findById(id);
    return entity != null ? Optional.of(toModel(entity)) : Optional.empty();
  }

  public Event createEvent(CreateEventRequest request) {
    EventEntity entity = new EventEntity();
    entity.name = request.getName();
    entity.type = EventEntity.EventType.fromString(request.getType().toString());
    entity.route = toEntityRoute(request.getRoute());
    entity.persist();

    return toModel(entity);
  }

  public Optional<Event> startEvent(String id) {
    EventEntity entity = EventEntity.findById(id);
    if (entity == null || entity.status != EventEntity.EventStatus.PLANNED) {
      return Optional.empty();
    }

    entity.status = EventEntity.EventStatus.STARTED;
    entity.startTime = LocalDateTime.now();

    // Start all participants
    for (EventEntity.Participant participant : entity.participants) {
      participant.status = EventEntity.Participant.ParticipantStatus.STARTED;
      participant.startTime = entity.startTime;
    }

    entity.persist();

    // Start simulation
    simulationService.startEventSimulation(id);

    return Optional.of(toModel(entity));
  }

  public Optional<Participant> addParticipant(String eventId, AddParticipantRequest request) {
    EventEntity entity = EventEntity.findById(eventId);
    if (entity == null) {
      return Optional.empty();
    }

    // Check if cyclist is already registered
    boolean alreadyRegistered =
        entity.participants.stream().anyMatch(p -> p.cyclistId.equals(request.getCyclistId()));

    if (alreadyRegistered) {
      return Optional.empty();
    }

    EventEntity.Participant participant = new EventEntity.Participant(request.getCyclistId());
    entity.participants.add(participant);
    entity.persist();

    return Optional.of(toModelParticipant(participant));
  }

  public List<LeaderboardEntry> getLeaderboard(String eventId) {
    // Get latest positions for all participants
    List<PositionEntity> latestPositions = PositionEntity.find("eventId = ?1", eventId).list();

    // Group by cyclist and get latest position for each
    var positionsByCyclist =
        latestPositions.stream()
            .collect(
                java.util.stream.Collectors.groupingBy(
                    p -> p.cyclistId,
                    java.util.stream.Collectors.maxBy(
                        java.util.Comparator.comparing(p -> p.timestamp))));

    // Convert to leaderboard entries and sort by distance
    var entries =
        positionsByCyclist.entrySet().stream()
            .filter(entry -> entry.getValue().isPresent())
            .map(
                entry -> {
                  PositionEntity pos = entry.getValue().get();
                  LeaderboardEntry entry1 = new LeaderboardEntry();
                  entry1.setCyclistId(pos.cyclistId);
                  entry1.setCyclistName("Cyclist " + pos.cyclistId); // TODO: Get actual name
                  entry1.setDistance(BigDecimal.valueOf(pos.distance));
                  entry1.setStatus(LeaderboardEntry.StatusEnum.RIDING);
                  entry1.setAverageSpeed(BigDecimal.valueOf(pos.speed)); // Simplified for MVP
                  return entry1;
                })
            .sorted((a, b) -> b.getDistance().compareTo(a.getDistance()))
            .toList();

    // Set positions
    for (int i = 0; i < entries.size(); i++) {
      entries.get(i).setPosition(i + 1);
    }

    return entries;
  }

  public List<Position> getCurrentPositions(String eventId) {
    List<PositionEntity> positions =
        PositionEntity.find("eventId = ?1 ORDER BY timestamp DESC", eventId).list();

    // Get latest position for each cyclist
    var latestPositions =
        positions.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    p -> p.cyclistId,
                    p -> p,
                    (existing, replacement) ->
                        existing.timestamp.isAfter(replacement.timestamp)
                            ? existing
                            : replacement));

    return latestPositions.values().stream().map(this::toModelPosition).toList();
  }

  private Event toModel(EventEntity entity) {
    Event event = new Event();
    event.setId(entity.id.toString());
    event.setName(entity.name);
    event.setType(Event.TypeEnum.fromValue(entity.type.getValue()));
    event.setStatus(Event.StatusEnum.fromValue(entity.status.getValue()));
    event.setRoute(toModelRoute(entity.route));
    event.setParticipants(entity.participants.stream().map(this::toModelParticipant).toList());
    if (entity.startTime != null) {
      event.setStartTime(OffsetDateTime.of(entity.startTime, ZoneOffset.UTC));
    }
    event.setCreatedAt(OffsetDateTime.of(entity.createdAt, ZoneOffset.UTC));
    return event;
  }

  private Route toModelRoute(EventEntity.Route entityRoute) {
    Route route = new Route();
    route.setName(entityRoute.name);
    route.setDistance(BigDecimal.valueOf(entityRoute.distance));
    route.setStartPoint(toModelGeoPoint(entityRoute.startPoint));
    route.setEndPoint(toModelGeoPoint(entityRoute.endPoint));
    if (entityRoute.waypoints != null) {
      route.setWaypoints(entityRoute.waypoints.stream().map(this::toModelWaypoint).toList());
    }
    return route;
  }

  private GeoPoint toModelGeoPoint(EventEntity.GeoPoint entityPoint) {
    GeoPoint point = new GeoPoint();
    point.setLatitude(BigDecimal.valueOf(entityPoint.latitude));
    point.setLongitude(BigDecimal.valueOf(entityPoint.longitude));
    return point;
  }

  private Waypoint toModelWaypoint(EventEntity.Waypoint entityWaypoint) {
    Waypoint waypoint = new Waypoint();
    waypoint.setPosition(toModelGeoPoint(entityWaypoint.position));
    waypoint.setDistance(BigDecimal.valueOf(entityWaypoint.distance));
    waypoint.setElevation(
        entityWaypoint.elevation != null ? BigDecimal.valueOf(entityWaypoint.elevation) : null);
    return waypoint;
  }

  private Participant toModelParticipant(EventEntity.Participant entityParticipant) {
    Participant participant = new Participant();
    participant.setCyclistId(entityParticipant.cyclistId);
    participant.setStatus(Participant.StatusEnum.fromValue(entityParticipant.status.getValue()));
    if (entityParticipant.startTime != null) {
      participant.setStartTime(OffsetDateTime.of(entityParticipant.startTime, ZoneOffset.UTC));
    }
    if (entityParticipant.finishTime != null) {
      participant.setFinishTime(OffsetDateTime.of(entityParticipant.finishTime, ZoneOffset.UTC));
    }
    return participant;
  }

  private Position toModelPosition(PositionEntity entity) {
    Position position = new Position();
    position.setCyclistId(entity.cyclistId);
    position.setLocation(
        toModelGeoPoint(
            new EventEntity.GeoPoint(entity.location.latitude, entity.location.longitude)));
    position.setTimestamp(OffsetDateTime.of(entity.timestamp, ZoneOffset.UTC));
    position.setDistance(BigDecimal.valueOf(entity.distance));
    position.setSpeed(BigDecimal.valueOf(entity.speed));
    position.setPower(entity.power != null ? BigDecimal.valueOf(entity.power) : null);
    return position;
  }

  private EventEntity.Route toEntityRoute(Route modelRoute) {
    EventEntity.Route route = new EventEntity.Route();
    route.name = modelRoute.getName();
    route.distance = modelRoute.getDistance().doubleValue();
    route.startPoint = toEntityGeoPoint(modelRoute.getStartPoint());
    route.endPoint = toEntityGeoPoint(modelRoute.getEndPoint());
    if (modelRoute.getWaypoints() != null) {
      route.waypoints = modelRoute.getWaypoints().stream().map(this::toEntityWaypoint).toList();
    }
    return route;
  }

  private EventEntity.GeoPoint toEntityGeoPoint(GeoPoint modelPoint) {
    return new EventEntity.GeoPoint(
        modelPoint.getLatitude().doubleValue(), modelPoint.getLongitude().doubleValue());
  }

  private EventEntity.Waypoint toEntityWaypoint(Waypoint modelWaypoint) {
    return new EventEntity.Waypoint(
        toEntityGeoPoint(modelWaypoint.getPosition()),
        modelWaypoint.getDistance().doubleValue(),
        modelWaypoint.getElevation() != null ? modelWaypoint.getElevation().doubleValue() : null);
  }
}
