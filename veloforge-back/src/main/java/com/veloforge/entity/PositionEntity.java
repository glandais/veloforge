package com.veloforge.entity;

import java.time.LocalDateTime;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "positions")
public class PositionEntity extends PanacheMongoEntity {

  public String cyclistId;
  public String eventId;
  public GeoPoint location;
  public LocalDateTime timestamp;
  public double distance;
  public double speed;
  public Double power;

  public PositionEntity() {
    this.timestamp = LocalDateTime.now();
  }

  public PositionEntity(
      String cyclistId, String eventId, GeoPoint location, double distance, double speed) {
    this.cyclistId = cyclistId;
    this.eventId = eventId;
    this.location = location;
    this.distance = distance;
    this.speed = speed;
    this.timestamp = LocalDateTime.now();
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
}
