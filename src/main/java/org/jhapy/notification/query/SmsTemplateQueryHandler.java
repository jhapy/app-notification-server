package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.SmsTemplateDTO;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.converter.SmsTemplateConverter;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.repository.SmsTemplateRepository;
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
public class SmsTemplateQueryHandler implements BaseQueryHandler<SmsTemplate, SmsTemplateDTO> {
  private final SmsTemplateRepository repository;
  private final SmsTemplateConverter converter;
  private final MongoTemplate mongoTemplate;

  @QueryHandler
  public GetSmsTemplateByIdResponse getById(GetSmsTemplateByIdQuery query) {
    return new GetSmsTemplateByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetSmsTemplateBySmsActionResponse getBySmsAction(GetSmsTemplateBySmsActionQuery query) {
    return new GetSmsTemplateBySmsActionResponse(
        converter.asDTO(
            repository
                .findBySmsActionAndIsActiveIsTrue(query.getSmsAction())
                .orElseThrow(EntityNotFoundException::new),
            null));
  }

  @QueryHandler
  public GetAllSmsTemplatesResponse getAll(GetAllSmsTemplatesQuery query) {
    return new GetAllSmsTemplatesResponse(converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingSmsTemplateResponse findAnyMatchingSmsTemplate(
      FindAnyMatchingSmsTemplateQuery query) {
    Page<SmsTemplate> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingSmsTemplateResponse(converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingSmsTemplateResponse countAnyMatchingSmsTemplate(
      CountAnyMatchingSmsTemplateQuery query) {
    return new CountAnyMatchingSmsTemplateResponse(
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
                  where("smsAction").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<SmsTemplate, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  public Class<SmsTemplate> getEntityClass() {
    return SmsTemplate.class;
  }

  @Override
  public GenericMapper<SmsTemplate, SmsTemplateDTO> getConverter() {
    return converter;
  }
}
