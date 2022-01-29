package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.SmsCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsUpdatedEvent;
import org.jhapy.dto.domain.notification.SmsDTO;
import org.jhapy.notification.command.SmsAggregate;
import org.jhapy.notification.domain.Sms;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class SmsConverter extends GenericMapper<Sms, SmsDTO> {
  public static SmsConverter INSTANCE = Mappers.getMapper(SmsConverter.class);

  public abstract SmsCreatedEvent toSmsCreatedEvent(SmsDTO dto);

  public abstract SmsUpdatedEvent toSmsUpdatedEvent(SmsDTO dto);

  public abstract void updateAggregateFromSmsCreatedEvent(
      SmsCreatedEvent event, @MappingTarget SmsAggregate aggregate);

  public abstract void updateAggregateFromSmsUpdatedEvent(
      SmsUpdatedEvent event, @MappingTarget SmsAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract Sms toEntity(SmsCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract Sms toEntity(SmsUpdatedEvent event);

  public abstract Sms asEntity(SmsDTO dto, @Context Map<String, Object> context);

  public abstract SmsDTO asDTO(Sms domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      SmsDTO dto, @MappingTarget Sms domain, @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      Sms domain, @MappingTarget SmsDTO dto, @Context Map<String, Object> context) {}
}
