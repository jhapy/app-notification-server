package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateUpdatedEvent;
import org.jhapy.dto.domain.notification.CloudNotificationMessageTemplateDTO;
import org.jhapy.notification.command.CloudNotificationMessageTemplateAggregate;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class CloudNotificationMessageTemplateConverter
    extends GenericMapper<CloudNotificationMessageTemplate, CloudNotificationMessageTemplateDTO> {
  public static CloudNotificationMessageTemplateConverter INSTANCE =
      Mappers.getMapper(CloudNotificationMessageTemplateConverter.class);

  public abstract CloudNotificationMessageTemplateCreatedEvent
      toCloudNotificationMessageTemplateCreatedEvent(CloudNotificationMessageTemplateDTO dto);

  public abstract CloudNotificationMessageTemplateUpdatedEvent
      toCloudNotificationMessageTemplateUpdatedEvent(CloudNotificationMessageTemplateDTO dto);

  public abstract void updateAggregateFromCloudNotificationMessageTemplateCreatedEvent(
      CloudNotificationMessageTemplateCreatedEvent event,
      @MappingTarget CloudNotificationMessageTemplateAggregate aggregate);

  public abstract void updateAggregateFromCloudNotificationMessageTemplateUpdatedEvent(
      CloudNotificationMessageTemplateUpdatedEvent event,
      @MappingTarget CloudNotificationMessageTemplateAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudNotificationMessageTemplate toEntity(
      CloudNotificationMessageTemplateCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudNotificationMessageTemplate toEntity(
      CloudNotificationMessageTemplateUpdatedEvent event);

  public abstract CloudNotificationMessageTemplate asEntity(
      CloudNotificationMessageTemplateDTO dto, @Context Map<String, Object> context);

  public abstract CloudNotificationMessageTemplateDTO asDTO(
      CloudNotificationMessageTemplate domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      CloudNotificationMessageTemplateDTO dto,
      @MappingTarget CloudNotificationMessageTemplate domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      CloudNotificationMessageTemplate domain,
      @MappingTarget CloudNotificationMessageTemplateDTO dto,
      @Context Map<String, Object> context) {}
}
