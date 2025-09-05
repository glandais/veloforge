package com.veloforge.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "events")
public class EventEntity extends PanacheMongoEntity {

  public String name;
  public EventType type;
  public EventStatus status;

  public Route route;
  public List<Participant> participants = new ArrayList<>();
  public LocalDateTime startTime;
  public LocalDateTime createdAt;

  public EventEntity() {
    this.createdAt = LocalDateTime.now();
    this.status = EventStatus.PLANNED;
  }

  public enum EventType {
    POINT_TO_POINT("point-to-point"),
    LOOP("loop"),
    BREVET("brevet");

    private final String value;

    EventType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static EventType fromString(String value) {
      for (EventType type : EventType.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      throw new IllegalArgumentException("Unknown event type: " + value);
    }
  }

  public enum EventStatus {
    PLANNED("planned"),
    STARTED("started"),
    FINISHED("finished");

    private final String value;

    EventStatus(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static EventStatus fromString(String value) {
      for (EventStatus status : EventStatus.values()) {
        if (status.value.equals(value)) {
          return status;
        }
      }
      throw new IllegalArgumentException("Unknown event status: " + value);
    }
  }

  public static class Route {
    public String name;
    public double distance;
    public GeoPoint startPoint;
    public GeoPoint endPoint;
    public List<Waypoint> waypoints = new ArrayList<>();

    public Route() {}

    public Route(String name, double distance, GeoPoint startPoint, GeoPoint endPoint) {
      this.name = name;
      this.distance = distance;
      this.startPoint = startPoint;
      this.endPoint = endPoint;
    }
  }

  public static class GeoPoint {
    public double latitude;
    public double longitude;

    public GeoPoint() {}

    public GeoPoint(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
    }
  }

  public static class Waypoint {
    public GeoPoint position;
    public double distance;
    public Double elevation;

    public Waypoint() {}

    public Waypoint(GeoPoint position, double distance, Double elevation) {
      this.position = position;
      this.distance = distance;
      this.elevation = elevation;
    }
  }

  public static class Participant {
    public String cyclistId;
    public ParticipantStatus status;

    public LocalDateTime startTime;
    public LocalDateTime finishTime;

    public Participant() {
      this.status = ParticipantStatus.REGISTERED;
    }

    public Participant(String cyclistId) {
      this.cyclistId = cyclistId;
      this.status = ParticipantStatus.REGISTERED;
    }

    public enum ParticipantStatus {
      REGISTERED("registered"),
      STARTED("started"),
      RIDING("riding"),
      RESTING("resting"),
      FINISHED("finished"),
      DNF("dnf");

      private final String value;

      ParticipantStatus(String value) {
        this.value = value;
      }

      public String getValue() {
        return value;
      }

      public static ParticipantStatus fromString(String value) {
        for (ParticipantStatus status : ParticipantStatus.values()) {
          if (status.value.equals(value)) {
            return status;
          }
        }
        throw new IllegalArgumentException("Unknown participant status: " + value);
      }
    }
  }
}
