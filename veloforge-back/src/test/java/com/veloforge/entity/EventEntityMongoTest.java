package com.veloforge.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class EventEntityMongoTest {

  @Test
  public void testEventEntityMongoSerialization() {
    // Create an event with enum values
    EventEntity event = new EventEntity();
    event.name = "Test Event";
    event.type = EventEntity.EventType.POINT_TO_POINT;
    event.status = EventEntity.EventStatus.PLANNED;
    event.startTime = LocalDateTime.now();
    
    // Create route
    EventEntity.Route route = new EventEntity.Route();
    route.name = "Test Route";
    route.distance = 100.0;
    route.startPoint = new EventEntity.GeoPoint(48.8566, 2.3522); // Paris
    route.endPoint = new EventEntity.GeoPoint(48.4469, 1.4869);   // Chartres
    event.route = route;
    
    // Persist the entity - this should serialize enums to string values
    event.persist();
    
    // Verify the entity was saved with an ID
    assertNotNull(event.id, "Event should have an ID after persistence");
    
    // Find the event back from the database
    EventEntity foundEvent = EventEntity.findById(event.id);
    assertNotNull(foundEvent, "Event should be retrievable from database");
    
    // Verify enum values are properly deserialized
    assertEquals(EventEntity.EventType.POINT_TO_POINT, foundEvent.type);
    assertEquals("point-to-point", foundEvent.type.getValue());
    
    assertEquals(EventEntity.EventStatus.PLANNED, foundEvent.status);
    assertEquals("planned", foundEvent.status.getValue());
    
    // Clean up
    event.delete();
  }
  
  @Test
  public void testParticipantStatusSerialization() {
    // Create an event with a participant
    EventEntity event = new EventEntity();
    event.name = "Test Event with Participant";
    event.type = EventEntity.EventType.LOOP;
    event.status = EventEntity.EventStatus.STARTED;
    event.startTime = LocalDateTime.now();
    
    // Create route
    EventEntity.Route route = new EventEntity.Route();
    route.name = "Test Route";
    route.distance = 50.0;
    route.startPoint = new EventEntity.GeoPoint(48.8566, 2.3522);
    route.endPoint = new EventEntity.GeoPoint(48.8566, 2.3522); // Loop back to start
    event.route = route;
    
    // Add a participant
    EventEntity.Participant participant = new EventEntity.Participant("cyclist-123");
    participant.status = EventEntity.Participant.ParticipantStatus.RIDING;
    event.participants.add(participant);
    
    // Persist the entity
    event.persist();
    
    // Find the event back
    EventEntity foundEvent = EventEntity.findById(event.id);
    assertNotNull(foundEvent);
    
    // Verify participant status is properly deserialized
    assertEquals(1, foundEvent.participants.size());
    EventEntity.Participant foundParticipant = foundEvent.participants.get(0);
    assertEquals(EventEntity.Participant.ParticipantStatus.RIDING, foundParticipant.status);
    assertEquals("riding", foundParticipant.status.getValue());
    
    // Clean up
    event.delete();
  }
}