package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.queryhandling.QueryHandler;
import org.jhapy.cqrs.query.notification.*;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.jhapy.dto.domain.notification.CloudNotificationMessageTemplateDTO;
import org.jhapy.notification.converter.CloudNotificationMessageTemplateConverter;
import org.jhapy.notification.converter.GenericMapper;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.repository.CloudNotificationMessageTemplateRepository;
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
public class CloudNotificationMessageTemplateQueryHandler
    implements BaseQueryHandler<
        CloudNotificationMessageTemplate, CloudNotificationMessageTemplateDTO> {
  private final CloudNotificationMessageTemplateRepository repository;
  private final CloudNotificationMessageTemplateConverter converter;
  private final MongoTemplate mongoTemplate;

  @QueryHandler
  public GetCloudNotificationMessageTemplateByIdResponse getById(
      GetCloudNotificationMessageTemplateByIdQuery query) {
    return new GetCloudNotificationMessageTemplateByIdResponse(
        converter.asDTO(
            repository.findById(query.getId()).orElseThrow(EntityNotFoundException::new), null));
  }

  @QueryHandler
  public GetCloudNotificationMessageTemplateByCloudNotificationMessageActionResponse
      getByCloudNotificationMessageAction(
          GetCloudNotificationMessageTemplateByCloudNotificationMessageActionQuery query) {
    return new GetCloudNotificationMessageTemplateByCloudNotificationMessageActionResponse(
        converter.asDTO(
            repository
                .findByCloudNotificationMessageActionAndIsActiveIsTrue(
                    query.getCloudNotificationMessageAction())
                .orElseThrow(EntityNotFoundException::new),
            null));
  }

  @QueryHandler
  public GetAllCloudNotificationMessageTemplatesResponse getAll(
      GetAllCloudNotificationMessageTemplatesQuery query) {
    return new GetAllCloudNotificationMessageTemplatesResponse(
        converter.asDTOList(repository.findAll(), null));
  }

  @QueryHandler
  public FindAnyMatchingCloudNotificationMessageTemplateResponse
      findAnyMatchingCloudNotificationMessageTemplate(
          FindAnyMatchingCloudNotificationMessageTemplateQuery query) {
    Page<CloudNotificationMessageTemplate> result =
        BaseQueryHandler.super.findAnyMatching(
            query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return new FindAnyMatchingCloudNotificationMessageTemplateResponse(
        converter.asDTOList(result.getContent(), null));
  }

  @QueryHandler
  public CountAnyMatchingCloudNotificationMessageTemplateResponse
      countAnyMatchingCloudNotificationMessageTemplate(
          CountAnyMatchingCloudNotificationMessageTemplateQuery query) {
    return new CountAnyMatchingCloudNotificationMessageTemplateResponse(
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
                  where("data").regex(filter),
                  where("cloudNotificationMessageAction").regex(filter)));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!andPredicated.isEmpty()) rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
  }

  @Override
  public MongoRepository<CloudNotificationMessageTemplate, UUID> getRepository() {
    return repository;
  }

  @Override
  public MongoTemplate getMongoTemplate() {
    return mongoTemplate;
  }

  @Override
  public Class<CloudNotificationMessageTemplate> getEntityClass() {
    return CloudNotificationMessageTemplate.class;
  }

  @Override
  public GenericMapper<CloudNotificationMessageTemplate, CloudNotificationMessageTemplateDTO>
      getConverter() {
    return converter;
  }
}
