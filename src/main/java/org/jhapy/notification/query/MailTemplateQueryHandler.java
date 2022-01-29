package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.MailTemplateDTO;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.converter.MailTemplateConverter;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.repository.MailTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class MailTemplateQueryHandler implements BaseQueryHandler<MailTemplate, MailTemplateDTO> {
  private final MailTemplateRepository repository;
  private final MailTemplateConverter converter;
  private final MongoTemplate mongoTemplate;

  @QueryHandler
  public GetMailTemplateByIdResponse getById(GetMailTemplateByIdQuery query) {
    return new GetMailTemplateByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetMailTemplateByMailActionResponse getByMailAction(
      GetMailTemplateByMailActionQuery query) {
    return new GetMailTemplateByMailActionResponse(
        converter.asDTO(
            repository
                .findByMailActionAndIsActiveIsTrue(query.getMailAction())
                .orElseThrow(EntityNotFoundException::new),
            null));
  }

  @QueryHandler
  public GetAllMailTemplatesResponse getAll(GetAllMailTemplatesQuery query) {
    return new GetAllMailTemplatesResponse(converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingMailTemplateResponse findAnyMatchingMailTemplate(
      FindAnyMatchingMailTemplateQuery query) {
    Page<MailTemplate> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingMailTemplateResponse(converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingMailTemplateResponse countAnyMatchingMailTemplate(
      CountAnyMatchingMailTemplateQuery query) {
    return new CountAnyMatchingMailTemplateResponse(
        BaseQueryHandler.super.countAnyMatching(query.getFilter(), query.getShowInactive()));
  }

  public void buildSearchQuery(Criteria rootCriteria, String filter, Boolean showInactive) {
    String loggerPrefix = getLoggerPrefix("buildSearchQuery");
    List<Criteria> andPredicated = new ArrayList<>();

    if (StringUtils.isNoneBlank(filter)) {
      andPredicated.add(
          (new Criteria())
              .orOperator(
                  where("name").regex(filter),
                  where("subject").regex(filter),
                  where("body").regex(filter),
                  where("bodyHtml").regex(filter),
                  where("copyTo").regex(filter),
                  where("from").regex(filter),
                  where("mailAction").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<MailTemplate, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  public Class<MailTemplate> getEntityClass() {
    return MailTemplate.class;
  }

  @Override
  public GenericMapper<MailTemplate, MailTemplateDTO> getConverter() {
    return converter;
  }
}
