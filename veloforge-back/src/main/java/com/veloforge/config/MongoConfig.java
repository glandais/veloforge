package com.veloforge.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import io.quarkus.arc.Unremovable;

@ApplicationScoped
public class MongoConfig {

  @Produces
  @Singleton
  @Unremovable
  public CodecProvider enumCodecProvider() {
    return new EnumCodecProvider();
  }

  public static class EnumCodecProvider implements CodecProvider {
    @Override
    @SuppressWarnings("unchecked")
    public <T> org.bson.codecs.Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      if (clazz == com.veloforge.entity.EventEntity.EventType.class) {
        return (org.bson.codecs.Codec<T>) new EventTypeCodec();
      }
      if (clazz == com.veloforge.entity.EventEntity.EventStatus.class) {
        return (org.bson.codecs.Codec<T>) new EventStatusCodec();
      }
      if (clazz == com.veloforge.entity.EventEntity.Participant.ParticipantStatus.class) {
        return (org.bson.codecs.Codec<T>) new ParticipantStatusCodec();
      }
      return null;
    }
  }
}
