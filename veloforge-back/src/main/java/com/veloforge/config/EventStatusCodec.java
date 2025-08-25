package com.veloforge.config;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.veloforge.entity.EventEntity.EventStatus;

public class EventStatusCodec implements Codec<EventStatus> {

  @Override
  public EventStatus decode(BsonReader reader, DecoderContext decoderContext) {
    String value = reader.readString();
    return EventStatus.fromString(value);
  }

  @Override
  public void encode(BsonWriter writer, EventStatus value, EncoderContext encoderContext) {
    writer.writeString(value.getValue());
  }

  @Override
  public Class<EventStatus> getEncoderClass() {
    return EventStatus.class;
  }
}