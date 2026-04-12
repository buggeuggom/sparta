package com.sparta.msa.example_part_3.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

  private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

  @Override
  public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
    return localDateTime == null ? null :
        Timestamp.from(localDateTime.atZone(KOREA_ZONE)
            .withZoneSameInstant(ZoneOffset.UTC)
            .toInstant());
  }

  @Override
  public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
    return timestamp == null ? null :
        timestamp.toInstant()
            .atZone(ZoneOffset.UTC)
            .withZoneSameInstant(KOREA_ZONE)
            .toLocalDateTime();
  }
}

