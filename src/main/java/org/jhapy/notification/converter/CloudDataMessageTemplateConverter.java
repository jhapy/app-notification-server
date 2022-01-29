package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateUpdatedEvent;
import org.jhapy.dto.domain.notification.CloudDataMessageTemplateDTO;
import org.jhapy.notification.command.CloudDataMessageTemplateAggregate;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class CloudDataMessageTemplateConverter
    extends GenericMapper<CloudDataMessageTemplate, CloudDataMessageTemplateDTO> {
  public static CloudDataMessageTemplateConverter INSTANCE =
      Mappers.getMapper(CloudDataMessageTemplateConverter.class);

  public abstract CloudDataMessageTemplateCreatedEvent toCloudDataMessageTemplateCreatedEvent(
      CloudDataMessageTemplateDTO dto);

  public abstract CloudDataMessageTemplateUpdatedEvent toCloudDataMessageTemplateUpdatedEvent(
      CloudDataMessageTemplateDTO dto);

  public abstract void updateAggregateFromCloudDataMessageTemplateCreatedEvent(
      CloudDataMessageTemplateCreatedEvent event,
      @MappingTarget CloudDataMessageTemplateAggregate aggregate);

  public abstract void updateAggregateFromCloudDataMessageTemplateUpdatedEvent(
      CloudDataMessageTemplateUpdatedEvent event,
      @MappingTarget CloudDataMessageTemplateAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudDataMessageTemplate toEntity(CloudDataMessageTemplateCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract CloudDataMessageTemplate toEntity(CloudDataMessageTemplateUpdatedEvent event);

  public abstract CloudDataMessageTemplate asEntity(
      CloudDataMessageTemplateDTO dto, @Context Map<String, Object> context);

  public abstract CloudDataMessageTemplateDTO asDTO(
      CloudDataMessageTemplate domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      CloudDataMessageTemplateDTO dto,
      @MappingTarget CloudDataMessageTemplate domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      CloudDataMessageTemplate domain,
      @MappingTarget CloudDataMessageTemplateDTO dto,
      @Context Map<String, Object> context) {}
}
