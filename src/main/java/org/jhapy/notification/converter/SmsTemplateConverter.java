package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.SmsTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateUpdatedEvent;
import org.jhapy.dto.domain.notification.SmsTemplateDTO;
import org.jhapy.notification.command.SmsTemplateAggregate;
import org.jhapy.notification.domain.SmsTemplate;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class SmsTemplateConverter extends GenericMapper<SmsTemplate, SmsTemplateDTO> {
  public static SmsTemplateConverter INSTANCE = Mappers.getMapper(SmsTemplateConverter.class);

  public abstract SmsTemplateCreatedEvent toSmsTemplateCreatedEvent(SmsTemplateDTO dto);

  public abstract SmsTemplateUpdatedEvent toSmsTemplateUpdatedEvent(SmsTemplateDTO dto);

  public abstract void updateAggregateFromSmsTemplateCreatedEvent(
      SmsTemplateCreatedEvent event, @MappingTarget SmsTemplateAggregate aggregate);

  public abstract void updateAggregateFromSmsTemplateUpdatedEvent(
      SmsTemplateUpdatedEvent event, @MappingTarget SmsTemplateAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract SmsTemplate toEntity(SmsTemplateCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract SmsTemplate toEntity(SmsTemplateUpdatedEvent event);

  public abstract SmsTemplate asEntity(SmsTemplateDTO dto, @Context Map<String, Object> context);

  public abstract SmsTemplateDTO asDTO(SmsTemplate domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      SmsTemplateDTO dto,
      @MappingTarget SmsTemplate domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      SmsTemplate domain,
      @MappingTarget SmsTemplateDTO dto,
      @Context Map<String, Object> context) {}
}
