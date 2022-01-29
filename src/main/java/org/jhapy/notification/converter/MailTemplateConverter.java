package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.MailTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateUpdatedEvent;
import org.jhapy.dto.domain.notification.MailTemplateDTO;
import org.jhapy.notification.command.MailTemplateAggregate;
import org.jhapy.notification.domain.MailTemplate;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class MailTemplateConverter extends GenericMapper<MailTemplate, MailTemplateDTO> {
  public static MailTemplateConverter INSTANCE = Mappers.getMapper(MailTemplateConverter.class);

  public abstract MailTemplateCreatedEvent toMailTemplateCreatedEvent(MailTemplateDTO dto);

  public abstract MailTemplateUpdatedEvent toMailTemplateUpdatedEvent(MailTemplateDTO dto);

  public abstract void updateAggregateFromMailTemplateCreatedEvent(
      MailTemplateCreatedEvent event, @MappingTarget MailTemplateAggregate aggregate);

  public abstract void updateAggregateFromMailTemplateUpdatedEvent(
      MailTemplateUpdatedEvent event, @MappingTarget MailTemplateAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract MailTemplate toEntity(MailTemplateCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract MailTemplate toEntity(MailTemplateUpdatedEvent event);

  public abstract MailTemplate asEntity(MailTemplateDTO dto, @Context Map<String, Object> context);

  public abstract MailTemplateDTO asDTO(MailTemplate domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      MailTemplateDTO dto,
      @MappingTarget MailTemplate domain,
      @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      MailTemplate domain,
      @MappingTarget MailTemplateDTO dto,
      @Context Map<String, Object> context) {}
}
