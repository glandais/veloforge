package com.veloforge.config;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.veloforge.entity.EventEntity.Participant.ParticipantStatus;

public class ParticipantStatusCodec implements Codec<ParticipantStatus> {

  @Override
  public ParticipantStatus decode(BsonReader reader, DecoderContext decoderContext) {
    String value = reader.readString();
    return ParticipantStatus.fromString(value);
  }

  @Override
  public void encode(BsonWriter writer, ParticipantStatus value, EncoderContext encoderContext) {
    writer.writeString(value.getValue());
  }

  @Override
  public Class<ParticipantStatus> getEncoderClass() {
    return ParticipantStatus.class;
  }
}
