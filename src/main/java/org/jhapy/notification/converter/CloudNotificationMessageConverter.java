package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.CloudNotificationMessageCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageUpdatedEvent;
import org.jhapy.dto.domain.notification.CloudNotificationMessageDTO;
import org.jhapy.notification.command.CloudNotificationMessageAggregate;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class CloudNotificationMessageConverter
    extends GenericMapper<CloudNotificationMessage, CloudNotificationMessageDTO> {
  public static CloudNotificationMessageConverter INSTANCE =
      Mappers.getMapper(CloudNotificationMessageConverter.class);

  public abstract CloudNotificationMessageCreatedEvent toCloudNotificationMessageCreatedEvent(
      CloudNotificationMessageDTO dto);

  public abstract CloudNotificationMessageUpdatedEvent toCloudNotificationMessageUpdatedEvent(
      CloudNotificationMessageDTO dto);

  public abstract void updateAggregateFromCloudNotificationMessageCreatedEvent(
      CloudNotificationMessageCreatedEvent event,
      @MappingTarget CloudNotificationMessageAggregate aggregate);

  public abstract void updateAggregateFromCloudNotificationMessageUpdatedEvent(
      CloudNotificationMessageUpdatedEvent event,
      @MappingTarget CloudNotificationMessageAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudNotificationMessage toEntity(CloudNotificationMessageCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudNotificationMessage toEntity(CloudNotificationMessageUpdatedEvent event);

  public abstract CloudNotificationMessage asEntity(
      CloudNotificationMessageDTO dto, @Context Map<String, Object> context);

  public abstract CloudNotificationMessageDTO asDTO(
      CloudNotificationMessage domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      CloudNotificationMessageDTO dto,
      @MappingTarget CloudNotificationMessage domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      CloudNotificationMessage domain,
      @MappingTarget CloudNotificationMessageDTO dto,
      @Context Map<String, Object> context) {}
}
