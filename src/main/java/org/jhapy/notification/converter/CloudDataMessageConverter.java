package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.CloudDataMessageCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageUpdatedEvent;
import org.jhapy.dto.domain.notification.CloudDataMessageDTO;
import org.jhapy.notification.command.CloudDataMessageAggregate;
import org.jhapy.notification.domain.CloudDataMessage;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class CloudDataMessageConverter
    extends GenericMapper<CloudDataMessage, CloudDataMessageDTO> {
  public static CloudDataMessageConverter INSTANCE =
      Mappers.getMapper(CloudDataMessageConverter.class);

  public abstract CloudDataMessageCreatedEvent toCloudDataMessageCreatedEvent(
      CloudDataMessageDTO dto);

  public abstract CloudDataMessageUpdatedEvent toCloudDataMessageUpdatedEvent(
      CloudDataMessageDTO dto);

  public abstract void updateAggregateFromCloudDataMessageCreatedEvent(
      CloudDataMessageCreatedEvent event, @MappingTarget CloudDataMessageAggregate aggregate);

  public abstract void updateAggregateFromCloudDataMessageUpdatedEvent(
      CloudDataMessageUpdatedEvent event, @MappingTarget CloudDataMessageAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudDataMessage toEntity(CloudDataMessageCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudDataMessage toEntity(CloudDataMessageUpdatedEvent event);

  public abstract CloudDataMessage asEntity(
      CloudDataMessageDTO dto, @Context Map<String, Object> context);

  public abstract CloudDataMessageDTO asDTO(
      CloudDataMessage domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      CloudDataMessageDTO dto,
      @MappingTarget CloudDataMessage domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      CloudDataMessage domain,
      @MappingTarget CloudDataMessageDTO dto,
      @Context Map<String, Object> context) {}
}
