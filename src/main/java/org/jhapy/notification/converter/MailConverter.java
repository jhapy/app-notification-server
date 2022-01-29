package org.jhapy.notification.converter;

import org.jhapy.cqrs.event.notification.MailCreatedEvent;
import org.jhapy.cqrs.event.notification.MailUpdatedEvent;
import org.jhapy.dto.domain.notification.MailDTO;
import org.jhapy.notification.command.MailAggregate;
import org.jhapy.notification.domain.Mail;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(config = BaseNosqlDbConverterConfig.class, componentModel = "spring")
public abstract class MailConverter extends GenericMapper<Mail, MailDTO> {
  public static MailConverter INSTANCE = Mappers.getMapper(MailConverter.class);

  public abstract MailCreatedEvent toMailCreatedEvent(MailDTO dto);

  public abstract MailUpdatedEvent toMailUpdatedEvent(MailDTO dto);

  public abstract void updateAggregateFromMailCreatedEvent(
      MailCreatedEvent event, @MappingTarget MailAggregate aggregate);

  public abstract void updateAggregateFromMailUpdatedEvent(
      MailUpdatedEvent event, @MappingTarget MailAggregate aggregate);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract Mail toEntity(MailCreatedEvent event);

  @Mapping(target = "created", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "modified", ignore = true)
  @Mapping(target = "modifiedBy", ignore = true)
  public abstract Mail toEntity(MailUpdatedEvent event);

  public abstract Mail asEntity(MailDTO dto, @Context Map<String, Object> context);

  public abstract MailDTO asDTO(Mail domain, @Context Map<String, Object> context);

  @AfterMapping
  protected void afterConvert(
      MailDTO dto, @MappingTarget Mail domain, @Context Map<String, Object> context) {}

  @AfterMapping
  protected void afterConvert(
      Mail domain, @MappingTarget MailDTO dto, @Context Map<String, Object> context) {}
}
