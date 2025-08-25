package com.veloforge.config;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.veloforge.entity.EventEntity.EventType;

public class EventTypeCodec implements Codec<EventType> {

  @Override
  public EventType decode(BsonReader reader, DecoderContext decoderContext) {
    String value = reader.readString();
    return EventType.fromString(value);
  }

  @Override
  public void encode(BsonWriter writer, EventType value, EncoderContext encoderContext) {
    writer.writeString(value.getValue());
  }

  @Override
  public Class<EventType> getEncoderClass() {
    return EventType.class;
  }
}