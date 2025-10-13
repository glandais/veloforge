package com.veloforge.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit test class for EventEntity enum serialization/deserialization. Verifies that enums are
 * correctly converted to/from their string values using Jackson.
 */
public class EventEntityEnumTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testEventTypeSerializationDeserialization() throws Exception {
    // Test EventType.POINT_TO_POINT -> "point-to-point"
    EventEntity.EventType type = EventEntity.EventType.POINT_TO_POINT;
    String json = objectMapper.writeValueAsString(type);
    assertEquals("\"point-to-point\"", json);

    // Test "point-to-point" -> EventType.POINT_TO_POINT
    EventEntity.EventType deserialized = objectMapper.readValue(json, EventEntity.EventType.class);
    assertEquals(EventEntity.EventType.POINT_TO_POINT, deserialized);
  }

  @Test
  public void testEventStatusSerializationDeserialization() throws Exception {
    // Test EventStatus.STARTED -> "started"
    EventEntity.EventStatus status = EventEntity.EventStatus.STARTED;
    String json = objectMapper.writeValueAsString(status);
    assertEquals("\"STARTED\"", json);

    // Test "started" -> EventStatus.STARTED
    EventEntity.EventStatus deserialized =
        objectMapper.readValue(json, EventEntity.EventStatus.class);
    assertEquals(EventEntity.EventStatus.STARTED, deserialized);
  }

  @Test
  public void testParticipantStatusSerializationDeserialization() throws Exception {
    // Test ParticipantStatus.RIDING -> "riding"
    EventEntity.Participant.ParticipantStatus status =
        EventEntity.Participant.ParticipantStatus.RIDING;
    String json = objectMapper.writeValueAsString(status);
    assertEquals("\"RIDING\"", json);

    // Test "riding" -> ParticipantStatus.RIDING
    EventEntity.Participant.ParticipantStatus deserialized =
        objectMapper.readValue(json, EventEntity.Participant.ParticipantStatus.class);
    assertEquals(EventEntity.Participant.ParticipantStatus.RIDING, deserialized);
  }

  @Test
  public void testAllEventTypes() throws Exception {
    for (EventEntity.EventType type : EventEntity.EventType.values()) {
      String json = objectMapper.writeValueAsString(type);
      EventEntity.EventType deserialized =
          objectMapper.readValue(json, EventEntity.EventType.class);
      assertEquals(type, deserialized);
    }
  }

  @Test
  public void testAllEventStatuses() throws Exception {
    for (EventEntity.EventStatus status : EventEntity.EventStatus.values()) {
      String json = objectMapper.writeValueAsString(status);
      EventEntity.EventStatus deserialized =
          objectMapper.readValue(json, EventEntity.EventStatus.class);
      assertEquals(status, deserialized);
    }
  }

  @Test
  public void testAllParticipantStatuses() throws Exception {
    for (EventEntity.Participant.ParticipantStatus status :
        EventEntity.Participant.ParticipantStatus.values()) {
      String json = objectMapper.writeValueAsString(status);
      EventEntity.Participant.ParticipantStatus deserialized =
          objectMapper.readValue(json, EventEntity.Participant.ParticipantStatus.class);
      assertEquals(status, deserialized);
    }
  }
}
